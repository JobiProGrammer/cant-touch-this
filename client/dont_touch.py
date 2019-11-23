#! /usr/bin/python

import pygit2
import requests
from time import sleep

# Initialize repo
repo = pygit2.Repository('.')

# Get name
name = repo.config['user.email']

# Get project name
remote = repo.branches['master'].upstream.remote_name
url = repo.remotes[remote].url

# Send to server
server = 'http://localhost:8000/api/change/'

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
            'change': str(changes)
        }
        print(payload)
        requests.post(server, payload)
    sleep(10)
