#! /usr/bin/python

import pygit2
import re


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


# Stuff for diffs
repo = pygit2.Repository('.')
diffs = []
for diff in repo.diff('master'):
    path = diff.delta.old_file.path
    lines = []
    for hunk in diff.hunks:
        for line in hunk.lines:
            if line.new_lineno == -1:
                lines.append(line.old_lineno)
    diffs.append((path, lines))

print(diffs)

# Get name
name = repo.config['user.email']

