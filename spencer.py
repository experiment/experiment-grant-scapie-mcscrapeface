import requests
import bs4
import pudb

def get_link_from_soup_tag(tag):
    return tag.a['href']

def link_builder(tag):
    """
    takes a bs tag object
    returns the link of that li tag element
    """
    return "http://www.spencer.org" + get_link_from_soup_tag(tag)


def cook_soup(link):
    """
    takes a link as a string
    returns a bs soup object
    """
    response = requests.get(link)
    return bs4.BeautifulSoup(response.text)

def name_deadline_link(row):
    """
    takes a row from the table
    makes a tuple with (grant name, deadline, link)
    """
    cells = row.find_all("td")
    name = cells[0].get_text()
    deadline = cells[1].get_text()
    link = link_builder(cells[2])
    return (name, deadline, link)

def find_max_grant_amount(soup):
    """
    takes a soup object
    finds all money amounts
    returns highest in soup
    """
    content_div_text = soup.select('div#right')[0].get_text()
    for word in content_div_text.split():
        if "$" in word:
            print word




soup = cook_soup("http://www.spencer.org/apply")
rows = soup.find("table").find("tbody").find_all("tr")


# get name, deadline, and link to guidelines
grants = []
for i, row in enumerate(rows):
    if i != 0:
        grants.append(name_deadline_link(row))


# follow link to guidelines
for grant in grants:
# find amount
    soup = cook_soup(grant[2])
    print(grant[0])
    max_amount = find_max_grant_amount(soup)

# find program contact email address

# find more links





"""
I used this code when I was trying to parse the page at
http://www.spencer.org/what-we-fund
later, I found the page at 
http://www.spencer.org/apply
Which has a table that's easier to parse
"""
# # text of response is available at response.text
# response = requests.get('http://www.spencer.org/what-we-fund')

# # intiate soup object using bs4
# soup = bs4.BeautifulSoup(response.text)

# # parse soup object for div with id of 'left'
# left_div_grant_links = soup.select('div#left li.leaf')

# # get all links in the left div
# links = [link_builder(li) for li in left_div_grant_links]

# for link in links:
#     soup = cook_soup(link)
