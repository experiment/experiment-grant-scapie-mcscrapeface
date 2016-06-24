require 'grant_search.rb'

class GrantsController < ApplicationController
  
  def index
    if params[:q].present?
      @grants = Grant.order(params[:sort]).pg_search(params[:q]).filter(params.slice(:funder_by_id))
      render 'index-results'
    else
      @grants = Grant.all
      @funders = Funder.all
    end
  end

  def show
    @grant = Grant.find(params[:id])
    render 'show2'
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