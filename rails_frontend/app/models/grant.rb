require 'pg_search'

class Grant < ActiveRecord::Base
    include PgSearch
    self.table_name = "grants_grant"

    belongs_to :funder

    scope :organization_filter, -> (organization) {where organization: organization }

    # scope :funder_by_id, where('funder_id == ?', id)
    # scope :funder_by_id, -> (id) {where(:funder.id => id)}
    # scope :funder_by_id, includes(:funders).where('grant.funder=?', id)
    scope :funder_by_id, lambda { |funder_id| where('funder_id = ?', funder_id)}

    multisearchable :against => [:search_description, :search_link]

    pg_search_scope :pg_search, :against => [PgSearch::Configuration::JsonbColumn.new(:data, 'description'),
                                                PgSearch::Configuration::JsonbColumn.new(:data, 'organization'),
                                                PgSearch::Configuration::JsonbColumn.new(:data, 'contact_info_name'),
                                                PgSearch::Configuration::JsonbColumn.new(:data, 'name'),
                                                PgSearch::Configuration::JsonbColumn.new(:data, 'description')]

    def search_description
      data['description']
    end

    def search_link
      data['link']
    end       

end
