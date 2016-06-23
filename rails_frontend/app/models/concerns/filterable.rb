module Filterable
  extend ActiveSupport::Concern

  module ClassMethods
    def filter(filtering_params)
      results = self.where(nil)
      filtering_params.each do |key, value|
        # if key == "funder_by_id" and value != []
        #   value.each do |id|
        #     results = results.public_send(key, id) if value.present?
        #   end
        # else
          results = results.public_send(key, value) if value.present?
        # end
      end
      results
    end

    private
      def parse_funders(params)
      end

  end
end