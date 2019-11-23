import requests

localhost = "http://127.0.0.1:8000/api"


def test():
    # create some changes
    r = requests.post(localhost + "/change/",
                      {"path": "my/project", "project": "test_project", "email": "tester@nice.de",
                       "change": "[1, 2, 3]"})
    print(r.text if r.status_code == 200 else r.status_code)

    r = requests.post(localhost + "/change/",
                      {"path": "my/project", "project": "test_project", "email": "tester2@nice.de",
                       "change": "[4]"})

    print(r.text if r.status_code == 200 else r.status_code)

    r = requests.post(localhost + "/change/",
                      {"path": "my/file", "project": "test_project", "email": "tester2@nice.de",
                       "change": "[5]"})
    print(r.text if r.status_code == 200 else r.status_code)
    r = requests.get(localhost + "/change/", {"project": "test_project"})
    print(r.text if r.status_code == 200 else r.status_code)

    r = requests.get(localhost + "/file/", {"project": "test_project"})
    print(r.text if r.status_code == 200 else r.status_code)

    r = requests.get(localhost + "/file/", {"project": "test_project", "dict": True})
    print(r.text if r.status_code == 200 else r.status_code)

    r = requests.get(localhost + "/change/", {"path": "my/project", "project": "test_project"})
    # print({"changes": [{"email": "tester@nice.de", "change": "[1, 2, 3]"}]})
    print(r.text if r.status_code == 200 else r.status_code)

    r = requests.post(localhost + "/change/",
                      {"path": "my/project", "project": "test_project", "email": "tester@nice.de",
                       "change": "[1, 2]"})
    print(r.text if r.status_code == 200 else r.status_code)

    r = requests.get(localhost + "/change/", {"path": "my/project", "project": "test_project"})
    # print({"changes": [{"email": "tester@nice.de", "change": "[1, 2]"}]})
    print(r.text if r.status_code == 200 else r.status_code)

    r = requests.post(localhost + "/change/",
                      {"path": "my/project", "project": "test_project", "email": "tester@nice.de",
                       "change": "[]"})
    print(r.text if r.status_code == 200 else r.status_code)

    r = requests.post(localhost + "/change/",
                      {"path": "my/project", "project": "test_project", "email": "tester2@nice.de",
                       "change": "[]"})
    print(r.text if r.status_code == 200 else r.status_code)

    r = requests.get(localhost + "/file/", {"project": "test_project"})
    print(r.text if r.status_code == 200 else r.status_code)


if __name__ == "__main__":
    test()
