from django.conf import settings
from django.db import models


# Create your models here.

class Project(models.Model):
    name = models.CharField(max_length=255)


class File(models.Model):
    path = models.CharField(max_length=1024, unique=True)
    project = models.ForeignKey(Project, on_delete=models.CASCADE, null=True, related_name="files")


class Change(models.Model):
    user = models.ForeignKey(settings.AUTH_USER_MODEL, null=True, on_delete=models.SET_NULL)
    file = models.ForeignKey(File, null=False, on_delete=models.CASCADE, related_name="changes")


class Line(models.Model):
    change = models.ManyToManyField(Change, blank=False, related_name="lines")
