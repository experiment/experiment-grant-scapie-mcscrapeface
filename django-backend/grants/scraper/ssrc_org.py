# from grants.scraper.spencer import cook_soup
# from spencer import cook_soup
import re
import string
from grants.models import Grant, Funder

'''
not needed when chsnged
'''
import requests
import bs4
import pudb


'''
    my_grant['name'] = my_json["opportunityTitle"]
    my_grant['contact_info_email'] = my_json["synopsis"]["agencyContactEmail"]
    my_grant['contact_info_phone'] = my_json["synopsis"]["agencyPhone"]
    my_grant['contact_info_name'] = my_json["synopsis"]["agencyContactName"]
    my_grant['link'] = my_json["synopsis"]["fundingDescLinkUrl"]
    my_grant['deadline'] = my_json["synopsis"]["responseDate"]
    my_grant['description'] = my_json["synopsis"]["synopsisDesc"]
    my_grant['amount'] = my_json["synopsis"]["awardCeiling"]
    my_grant["type"] = "grant"
'''

PHONE = ""
ORG_NAME = "Social Science Research Council"


def cook_soup(link):
    """
    takes a link as a string
    returns a bs soup object
    """
    response = requests.get(link)
    return bs4.BeautifulSoup(response.text)


def get_link_from_soup_tag(tag):
    return tag.a['href']


def find_all_elements(soup, element):
    """
    takes
    a soup object
    and
    an element as a string, something like 'a'
    and returns a list
    where each element is a bs4.element.Tag object
    """
    return soup.findAll(element)


def find_all_elements_with_text(soup, element, text):
    """
    takes
    a soup object
    and
    an element as a string, something like 'a'
    and
    a text string, something like 'print as a PDF'
    and returns a list
    where each element is a bs4.element.Tag object
    """
    return soup.findAll(element, text=re.compile(text))


def find_first_element_with_text(soup, element, text):
    """
    takes
    a soup object
    and
    an element as a string, something like 'a'
    and
    a text string, something like 'print as a PDF'
    and returns a object
    which is a bs4.element.Tag object
    """
    return soup.find(element, text=re.compile(text))


def find_first_instance_of_text(soup, text):
    """
    takes
    a soup object
    and a string
    returns
    a bs4.element.Tag object
    that contains first instance of that text
    """
    return soup.find(text=re.compile(text))

def special_match(strg, search=re.compile(r'[^0-9,$]').search):
    return not bool(search(strg))


def get_money_from_str(_str):
    """
    take in a str like $500,000
    and returns an int like 500000
    """
    return int(strip_puncuation(_str))


def strip_money_and_comma(_str):
    """
    take in a str like $500,000
    and returns a str like 500000
    """
    return _str.replace("$", "").replace(",", "")


def only_digits(_str):
    return re.sub("\D", "", _str)


def split_monies(double_money_str, c='$'):
    """
    takes a str like '$750,000$750,000'
    and returns a list like ['$750,000', '$750,000']
    """
    last_index = len(double_money_str) - 1
    index_of_second_money = double_money_str[1:last_index].index(c)
    return [double_money_str[0:index_of_second_money + 1], double_money_str[index_of_second_money + 1:last_index + 1]]


def find_money_amount(_str):
    if len(_str) == 1:
        return False
    elif _str.count('$') > 1:
        return max([get_money_from_str(sub_str) for sub_str in split_monies(_str)])
    elif not special_match(_str):
        no_money_or_comma = strip_money_and_comma(_str)
        only_numbers = int(only_digits(no_money_or_comma))
        return only_numbers
    else:
        return get_money_from_str(_str)


def find_max_grant_amount(soup, sub_soup=None, link=None):
    """
    takes a soup object
    finds all money amounts
    returns highest in soup
    """
    monies = []
    # content_div_text = soup.select('div#right')[0].get_text()
    if sub_soup is not None:
        content_div_text = sub_soup[0].get_text()

    for word in content_div_text.split():
        if "$" in word:
            monies.append(find_money_amount(word))

    if len(monies) > 0:
        return max(monies)
    elif link is not None:
        return "Grant amount not given. Check " + link + " for more details."
    else:
        return "Grant amount not given."


def pull_number_from_element(navstr):
    """
    given a Navigablestring
    returns a phone number that's
    contained in the string
    """
    phone = []
    for i, char in enumerate(list(navstr)):
        if navstr[i].isdigit():
            while True:
                if navstr[i] != ' ':
                    phone.append(navstr[i])
                    i += 1
                else:
                    return ''.join(phone)

    return None


def clean_non_grants(li):
    """
    returns only those links
    who's titles contain words
    that are in the grants list
    """
    grants = ['fellowship', 'grant']
    text = li.text.lower()
    for grant in grants:
        if grant in text:
            return True
    return False


def pull_word_given_substing(substring, navstr):
    """
    takes
    a bs4 Navigablestring object
    and
    a substring
    returns
    the word in the Navigablestring which contains the substring
    """
    for word in navstr.split(" "):
        if substring in word:
            return word
    return None


def get_phone(grant_soup):
    """
    tries to find the phone on the page
    if it can't, then uses the last phone number found on
    other SSRC grants
    """
    global PHONE
    try:
        phone = pull_number_from_element(grant_soup.find(text=re.compile('Tel:')))
        PHONE = phone
        return phone
    except TypeError:
        return PHONE


def get_href_from_a(a):
    """
    try to get the href from a link
    """
    try:
        return a['href']
    except:
        return None


def get_staff(grant_soup):
    """
    if there's no email address on the grant page,
    things get complicated.
    I go hunting for it in a couple places

    first, I try looking if there's a link with 
    "http://www.ssrc.org/staff/"
    on the page. If so, I follow that link, and get the email address on the next page

    second, I try seeing if there's any links with
    "mailto:" on the page. If so, i get the href of that link
    """
    # get all links
    lis = grant_soup.findAll('a')
    # get all hrefs for links
    hrefs = [get_href_from_a(li) for li in lis]
    # see if href contains http://www.ssrc.org/staff/
    staff_link = ""
    for href in hrefs:
        for href in hrefs:
            if href is None:
                    pass
            elif "http://www.ssrc.org/staff/" in href:
                    staff_link = href
    # return that link
    # go to that page
    if staff_link != "":
        staff_soup = cook_soup(staff_link)
        email = pull_word_given_substing('@ssrc.org', find_first_instance_of_text(staff_soup, '@ssrc.org'))
        return email
    else:
        # try to find "contact program staff"
        for href in hrefs:
            if href is None:
                    pass
            elif "mailto:" in href:
                    return href.split(":")[1]

    return "None given :("
    # find @ssrc.org
    # return that email


def get_deadline_from_str(navstr):
    pu.db
    return 1


def get_email(grant_soup):
    try:
        return pull_word_given_substing('@ssrc.org', find_first_instance_of_text(grant_soup, '@ssrc.org'))
    except AttributeError:
        return get_staff(grant_soup)


def get_month_and_day(_str):
    _list = _str.split(" ")
    months = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December']
    for i, word in enumerate(_list):
        if _list[i] in months:
                month = word
                day = _list[i + 1]
    return month, day


def strip_puncuation(_str):
    return re.sub(r'\W+', '', _str)
    # return re.compile('[%s]' % re.escape(string.punctuation)).sub('', _str)


def get_year(soup, _str):
    # start at str
    # find next instance of '201'
    return strip_puncuation(_str.findNext(text=re.compile('201')).strip())


def solve(s):
    return re.sub(r'(\d)(st|nd|rd|th)', r'\1', s)


def char_check(_str):
    if strip_puncuation(_str).isdigit() is False and strip_puncuation(_str).isdigit() is False and strip_puncuation(solve(_str)).isdigit() is False:
        return True
    else:
        return False


def get_month_day_year(strong_els):
    """
    takes 
    a list of bs4.element.Tag objects
    where each is a strong element
    returns
    the month, day, and year
    if theres a date in the list
    """
    months = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December']
    for el in strong_els:
        text = el.text
        _list = text.split(" ")
        for i, word in enumerate(_list):
            if strip_puncuation(word) in months:
                month = word
                try:
                    if char_check(_list[i + 1]):
                        break
                    if list(_list[i])[-1] == ',':
                        day = strip_puncuation(_list[i - 1])
                        year = strip_puncuation(_list[i + 1])
                    elif _list[i + 1]:
                        day = strip_puncuation(_list[i + 1])
                        year = strip_puncuation(_list[i + 2])
                    return month, day, year
                except:
                    pass
    return None


def get_deadline(soup):
    # try 'deadline is'
    try:
        deadline_str = find_first_instance_of_text(soup, 'deadline is')
        if deadline_str is not None:
            month, day = get_month_and_day(deadline_str)
            year = get_year(soup, deadline_str)
            return str(month) + " " + str(day) + " " + str(year)

        # try to get strong deadline
        strong_els = find_all_elements(soup=soup, element='strong')
        if strong_els != []:
            month, day, year = get_month_day_year(strong_els)
            return str(month) + " " + str(day) + " " + str(year)
    except TypeError:
        return "no posted application deadline :("


def get_description(soup, link=None):
    try:
        return soup.select('#overview')[0].get_text()
    except:
        pass

    if link is not None:
        return "Description not given. Try " + link + " for more details."
    else:
        return "Description not given."


    # try:
    #     return get_deadline_from_str(find_first_instance_of_text(grant_soup, 'Applications must be submitted by'))
    # except:
    #     print "waa"


def run():
    # get http://www.ssrc.org/fellowships/
    soup = cook_soup("http://www.ssrc.org/fellowships/")

    # find "All Fellowships and Prizes"
    fellowship_header = find_first_element_with_text(soup=soup, element='h3', text='All Fellowships')

    # Find all links underneath this header
    ul = fellowship_header.findNext('ul')
    lis = ul.findAll('li')
    only_grants = [li for li in lis if clean_non_grants(li)]
    links = [get_link_from_soup_tag(li) for li in only_grants]

    for link in links:
        # go to link
        grant_soup = cook_soup(link)
        grant = {}
        grant["funder"] = ORG_NAME
        grant['name'] = grant_soup.title.get_text()
        grant['contact_info_email'] = get_email(grant_soup)
        grant['contact_info_phone'] = get_phone(grant_soup)
        grant['link'] = link
        grant['deadline'] = get_deadline(grant_soup)
        grant['amount'] = find_max_grant_amount(grant_soup, sub_soup=grant_soup.select('#overview'), link=link)
        grant['description'] = get_description(grant_soup, link)

        try:
            db_grant = Grant.objects.get(data__name=grant['name'])
            db_grant.update_updated()
        except:
            db_grant = Grant(data=grant)
            db_grant.save()

        funder, created = Funder.objects.get_or_create(name=ORG_NAME)
        db_grant.funder = funder
        db_grant.save()
