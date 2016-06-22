class Funder < ActiveRecord::Base
  self.table_name = 'grants_funder'

  has_many :grants
end
