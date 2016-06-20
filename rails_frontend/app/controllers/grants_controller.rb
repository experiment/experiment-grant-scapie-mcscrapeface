require 'grant_search.rb'

class GrantsController < ApplicationController
  def index

    # @grants = Grant.all
    @grant_search_form = GrantSearchForm.new
  end
end
