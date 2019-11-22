#! /usr/bin/python

from pygit2 import Repository

# Stuff for diffs
repo = Repository('.')
for diff in repo.diff('HEAD'):
    for hunk in diff.hunks:
        for line in hunk.lines:
            print(line.content)

# Get name
name = repo.config['user.email']

