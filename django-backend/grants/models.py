from __future__ import unicode_literals
from django.utils import timezone
from django.contrib.postgres.fields import JSONField
from django.db import models


class Funder(models.Model):
    name = models.CharField(max_length=500)

    def __str__(self):
        return self.name


# Create your models here.
class Grant(models.Model):
    funder = models.ForeignKey(Funder, null=True, blank=True, default=None)
    data = JSONField()
    created_at = models.DateTimeField(editable=False)
    updated_at = models.DateTimeField(null=True, blank=True, default=None)

    def __str__(self):
        return self.funder.name

    def update_updated(self):
        self.updated_at = timezone.now()
        self.save()

    def save(self, *args, **kwargs):
        self.updated_at = timezone.now()
        if not self.id:
            self.created_at = timezone.now()

        super(Grant, self).save(*args, **kwargs)
