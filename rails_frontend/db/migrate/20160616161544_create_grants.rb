class CreateGrants < ActiveRecord::Migration
  def change

    #  I've taken out this create table, because I'm not creating a table for this model. 
    # I'm connecting to an existing table that's created by the scraper, called `grants_grant`
    
    # create_table :grants do |t|

    #   t.timestamps null: false
    # end
  end
end
