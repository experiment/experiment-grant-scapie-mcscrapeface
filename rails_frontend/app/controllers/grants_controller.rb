require 'grant_search.rb'

class GrantsController < ApplicationController
  helper_method :sort_column, :sort_direction
  
  def index
    if params[:q].present?
      @grants = Grant.order(sort_column + ' ' + sort_direction).pg_search(params[:q]).filter(params.slice(:funder_by_id))
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

  private
    def sort_column
      %w[data->'name' data->'deadline'].include?(params[:sort]) ? params[:sort] : "data->'name'"
      # params[:sort] || "data->'name'"
    end

    def sort_direction
      %w[asc desc].include?(params[:direction]) ?  params[:direction] : "asc"
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