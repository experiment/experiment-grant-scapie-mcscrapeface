require 'grant_search.rb'

class GrantsController < ApplicationController
  def index
    if params[:q].present?
      binding.pry
      @grants = Grant.pg_search(params[:q]).pluck("data -> 'name'").uniq
      render 'index-results'
    else
      @grants = Grant.all
    end
  end

  def show
    @grant = Grant.find(params[:id])
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