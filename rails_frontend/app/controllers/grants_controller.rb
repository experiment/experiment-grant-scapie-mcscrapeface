require 'grant_search.rb'

class GrantsController < ApplicationController
  def index
    if params[:q].present?
      @grants = Grant.pg_search(params[:q])
      render 'index-results'
    end
  end
end


# def index
#     if params[:search].present?
#       scope = Opportunity.order('id asc').search(params[:search])
#     else
#       scope = Opportunity.order('id asc')
#     end
#     @opportunities = scope.page(params[:page]).per(20)
#   end