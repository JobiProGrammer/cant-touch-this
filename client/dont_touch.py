#! /usr/bin/python

import pygit2
import requests
from time import sleep
import click
import os
import json

SERVER = "52.236.180.203"

# Send to server
server = 'http://{}:8080/api/change/'.format(SERVER)

waitTime = 10


@click.command()
@click.option('--path', default=None, help='Path to the project root directory.')
def main(path):
    # Initialize with 60 so we always pull in the beginning
    wait_counter = 60

    if path != None:
        os.chdir(path)
        print("new path: " + path)

    # Initialize repo
    repo = pygit2.Repository('.')

    # Get name
    email = repo.config['user.email']
    name = repo.config['user.name']

    # Get project name
    remote = repo.branches['master'].upstream.remote_name
    url = repo.remotes[remote].url
    remote_head = repo.branches.remote[remote + '/master']


    # Stuff for diffs
    while True:
        # Get the latest changes
        changed_by_me = []

        response = requests.get(server, {'project': url})
        for entry in json.loads(response.text):
            for change in entry['changes']:
                if change['email']:
                    changed_by_me.append(entry['path'])
                    break

        # Update refs

        # Only fetch every minute
        if wait_counter == 60:
            os.system("git fetch " + remote)
            wait_counter = 0

        diffs = []
        for diff in repo.diff(remote_head):
            path = diff.delta.old_file.path
            lines = []
            for hunk in diff.hunks:
                for line in hunk.lines:
                    # Only check deleted lines
                    if line.new_lineno == -1:
                        lines.append(line.old_lineno)
            diffs.append((path, lines))

        for (path, changes) in diffs:
            payload = {
                'path': path,
                'project': url,
                'email': email,
                'username': name,
                'change': str(changes)
            }
            print(payload)
            requests.post(server, payload)

        paths = [x[0] for x in diffs]
        for path in changed_by_me:
            if path not in paths:
                payload = {
                    'path': path,
                    'project': url,
                    'email': email,
                    'username': name,
                    'change': '[]'
                }
                print(payload)
                requests.post(server, payload)

        sleep(waitTime)
        wait_counter += waitTime


if __name__ == "__main__":
    main()
