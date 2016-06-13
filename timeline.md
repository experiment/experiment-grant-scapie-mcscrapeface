# MVP

 - Grants Database for Higher Education, Education Research
 - Table lists all grants with:
     - organization
     - name
     - contact info
     - link
     - deadline
     - description
     - amount
     - type: award v grant
     - tags
 - Searchable by:
     - organization
     - amount
     - deadline
     - keywords
     - type
     - tags
 - Sortable by:
     - amount
     - deadline
     - type
 - Database scrapes grant sources at least once daily
 - populates database with source and json object of info about each grant from source
 - Scraper for each source has notification alert if it fails
 - Clicking on a grant goes to a page with details about that grant and links to the actual grant page
 - Grants database include at least 17 sources.
 - 4 - Humanities, Social Science, STEM
 - 4 - State and Federal Funding Sources
 - 5 - Foundations
 - 3 - Disserations
 - 1 - Search Engines
 -  see below for details...
    - Humanities, Social Science, STEM - 4
        - STEM
            - 1 - NSF
        - Humanities
            - 1 - National Endowment for the Humanities – Division of Education Programs
            - 2 - National Endowment for the Humanities – Office of Digital Humanities
                - http://www.neh.gov/grants
        - Social Studies
            - Social Science Research Council
            - http://www.ssrc.org/fellowships/
    - State and Federal Funding Sources - 4
        - U.S. Department of Education – Grants and Contracts
            - http://www.ed.gov/fund/landing.jhtml
        - National Institutes of Health
            - http://www.nih.gov
            - http://grants1.nih.gov/grants/guide/listserv.htm
        - National Science Foundation (NSF)
            - also in `Humanities, Social Science, STEM`
        - Institute of Education Sciences (IES)
            - http://www.ed.gov/about/offices/list/ies/index.html
            - http://ies.ed.gov/funding/index.asp#current
    - International Funding Sources
    - Foundations - 5
        - The American Educational Research Association (AERA)
            - http://www.aera.net/ProfessionalOpportunitiesFunding/FundingOpportunities/AERAGrantsProgram/tabid/10242/Default.aspx
        - Annenberg Foundation
            - http://www.annenbergfoundation.org/grants/
        - Bill & Melinda Gates Foundation
            - http://www.gatesfoundation.org
        - International Educational Research Foundation
            - http://www.ierf.org/grants.asp
        - Spencer Foundation
            - http://www.spencer.org/
    
    - Other Organizations
    - Dissertations - 3
        - International Reading Association, Inc
            - http://www.reading.org
        - American Association of University Women Educational Foundation
            - http://www.aauw.org/
        - Council for Learning Disabilities
            - http://www.cldinternational.org
    - Grant Search Engines - at least 1
        - Pivot
            - http://pivot.cos.com/funding/results


# Luxury Goals
- Subscribe to a search
    - update user with email if new grants/awards added to their search
- Track specific grants on profile
    - user can see table of tracked grants and links to details about that grant
- Organizations can list their grants on our site


# Week 1: June 13 - June 19

 - Mon
     - Talk to D/C about project
     - Try to make scraper for Spencer
     - make MVP, luxury goals, schedule


none


- Tues - Sun
    - Write blog post about plan for project
    - Makes scraper for IES
        - http://ies.ed.gov/funding/index.asp#current
    - Create Django project which runs celery/redis background tasks and scrapes spencer, IES
        - scraper scrapes once per day
        - populates database with org, json object
    - Connect Experiment DB with Django DB
    - Run query from Rails route to DB that queries spencer and IES info
    - Complete 5 other scrapers for 5 other organizations to run following this same pattern
    - Locate 3 researchers to ask about ED grants
        - masters student
        - PhD student
        - young professor
        - (old professor?)
    - Wednesday learning time
        - How to Scrape with Python

# Week 2: June 20 - June 26

- Complete remaining scrapers
- Look for larger scraper potential 
    - Natural language processing?
    - Scrapy?
- Connect all remaining scrapers to DB with org and json object
- Connect rails route to DB so that it can query for all organizations
- Start to create Rails front end database
- Front-end has table of all grants
- Front end has search bar for grants that accepts searchable terms listed above
- front-end has sort options for grants that accepts sort options listed above
- Contact 3 researchers
    - What resources do you use?
    - What would you like in a grants search?
    - What do you think of my grants search?
    - What would you improve?



# Week 3: June 27 - July 1

- Polish front-end
- Use Experiment style guide
- Allow additional features when User is logged in
    - See luxury goals



