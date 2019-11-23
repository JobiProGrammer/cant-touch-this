#! /usr/bin/python

import pygit2
import re
import requests
from time import sleep


def get_function_name_from_line(path, lineno):
    with open(path) as file:
        # Check if we're in the main function
        if not re.match('( |\t|def)', path[lineno]):
            return '__main__'

        for i in range(lineno, -1, -1):
            # Split the 'def' off
            split = path[i].split(maxsplit=1)
            if split[0] == 'def':
                # Split off the parameters
                return split[1].split('(', maxsplit=1)[0]

        raise RuntimeError("Indented but not in function")


# Initialize repo
repo = pygit2.Repository('.')

# Get name
name = repo.config['user.email']

# Get project name
remote = repo.branches['master'].upstream.remote_name
url = repo.remotes[remote].url

# Send to server
server = 'PLACEHOLDER'

# Stuff for diffs
while True:
    diffs = []
    for diff in repo.diff('master'):
        path = diff.delta.old_file.path
        lines = []
        for hunk in diff.hunks:
            for line in hunk.lines:
                # Only check deleted lines
                if line.new_lineno == -1:
                    lines.append(line.old_lineno)

        # Ignore files which only have added lines
        if len(lines) != 0:
            diffs.append((path, lines))

    for (path, changes) in diffs:
        payload = {
            'path': path,
            'project': url,
            'email': name,
            'change': changes
        }
        print(payload)
        requests.post(server, payload)
    sleep(10)
