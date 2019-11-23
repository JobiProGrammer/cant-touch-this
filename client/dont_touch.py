#! /usr/bin/python

from pygit2 import Repository
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
repo = Repository('.')
for diff in repo.diff('HEAD'):
    for hunk in diff.hunks:
        for line in hunk.lines:
            print(line.content)

# Get name
name = repo.config['user.email']

