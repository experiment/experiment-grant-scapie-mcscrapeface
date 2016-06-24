import spencer
import ies
import ssrc_org


def scrape():
    ssrc_org.run()
    spencer.run()
    ies.run()


def save_to_db(grant_as_json):
    """
    takes a list of grants
    where each element of the list is a json object
    and saves that grant to the db
    as a object of type Grant
    """
    pass
