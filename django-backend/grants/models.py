from __future__ import unicode_literals
from django.utils import timezone
from django.contrib.postgres.fields import JSONField
from django.db import models


# Create your models here.
class Grant(models.Model):
    organization = models.CharField(max_length=500)
    data = JSONField()
    created_at = models.DateTimeField(editable=False)
    updated_at = models.DateTimeField(null=True, blank=True, default=None)

    def __str__(self):
        return self.organization

    def save(self, *args, **kwargs):
        self.updated_at = timezone.now()
        if not self.id:
            self.created_at = timezone.now()

        super(Grant, self).save(*args, **kwargs)
