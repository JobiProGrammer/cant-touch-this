from django.contrib import admin

# Register your models here.
from main.models import Project, File, Line, Change

admin.site.register(Project)
admin.site.register(File)
admin.site.register(Change)
admin.site.register(Line)
