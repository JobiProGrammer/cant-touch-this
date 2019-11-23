from django.urls import path

from main.views import FileView, ChangeView, ProjectView

urlpatterns = [
    # path('get_changes', views.get_changes, name='get_changes'),
    # path('get_edited_files', views.get_edited_files, name='get_edited_files'),
    path('file/', FileView.as_view()),
    path('change/', ChangeView.as_view()),
    path('project/', ProjectView.as_view()),
]
