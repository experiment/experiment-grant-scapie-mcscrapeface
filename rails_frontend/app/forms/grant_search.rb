class GrantSearchForm
    include ActiveModel::Model

    attr_accessor :search

    validates :search, presence: true
end