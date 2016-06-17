from celery.task.schedules import crontab
from celery.decorators import periodic_task
from celery.utils.log import get_task_logger

from grants.scraper.scraper import scrape

logger = get_task_logger(__name__)


@periodic_task(
    run_every=(crontab(hour='*/1')),
    name="task_scrape_grants",
    ignore_result=True
)
def task_scrape_grants():
    """
    Saves latest image from Flickr
    """
    scrape()
    logger.info("Scraped")
