from typing import List, Dict

from django.conf import settings
from django.db import models


# Create your models here.

class Project(models.Model):
    name = models.CharField(max_length=255, unique=True)

    def __str__(self):
        return self.name


class File(models.Model):
    path = models.CharField(max_length=1024)
    project = models.ForeignKey(Project, on_delete=models.CASCADE, null=True, related_name="files")

    class Meta:
        unique_together = ('path', 'project')

    def __str__(self):
        return self.path

    def get_changes(self) -> (List[List[int]], List[str]):
        changes = self.changes.all()
        ch = []
        us = []
        for i in changes:
            ch.append([j.id for j in i.lines.all()])
            us.append(i.user.email)
        return ch, us

    def get_changes_dict(self) -> Dict:
        changes, user = self.get_changes()
        re_dict = {"path": self.path, "project": self.project.name}
        ch = []
        for index, i in enumerate(user):
            ch.append({"email": i, "lines": changes[index]})

        re_dict["changes"] = ch
        return re_dict

    def create_change(self, change: List[int], user: settings.AUTH_USER_MODEL):
        # u = settings.AUTH_USER_MODEL.objects.get(email=user_email)
        c = Change(user=user, file=self)
        # change should be a list of all lines
        for i in change:
            # check if we can find line i --> if not then create...
            line = Line.objects.get_or_create(id=i)
            c.lines.add(line)
        c.save()
        return self.get_changes_dict()

        # c.lines.add(change)
        # c.save()


class Line(models.Model):
    pass

    def __str__(self):
        return str(self.id)


class Change(models.Model):
    user = models.ForeignKey(settings.AUTH_USER_MODEL, null=True, on_delete=models.SET_NULL, related_name="changes")
    file = models.ForeignKey(File, null=False, on_delete=models.CASCADE, related_name="changes")
    lines = models.ManyToManyField(Line, blank=True, related_name="change")

    def __str__(self):
        return str([i.id for i in self.lines.all()])
