class Grant < ActiveRecord::Base
    include PgSearch
    self.table_name = "grants_grant"
    multisearchable :against => [:search_description, :search_link]

    pg_search_scope :pg_search_description, :against => PgSearch::Configuration::JsonbColumn.new(:data, 'description')

    def search_description
      data['description']
    end

    def search_link
      data['link']
    end
end
