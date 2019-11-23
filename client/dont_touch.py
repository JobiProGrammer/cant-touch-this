#! /usr/bin/python

import pygit2
import requests
from time import sleep
import click
import os

SERVER = "52.236.180.203"

# Initialize repo
repo = pygit2.Repository('.')

# Get name
name = repo.config['user.email']

# Get project name
remote = repo.branches['master'].upstream.remote_name
url = repo.remotes[remote].url

# Send to server
server = 'http://{}:8080/api/change/'.format(SERVER)

@click.command()
@click.option('--path', default=None, help='Path to the project root directory.')
def main(path):
    if path != None:
        os.chdir(path)
    
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
        
if __name__ == "__main__":
    main()
