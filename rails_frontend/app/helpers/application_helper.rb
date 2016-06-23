module ApplicationHelper
  def check_params_for_funder(params, funder)
    if params[:funder_by_id]
      params[:funder_by_id].include?(funder.id.to_s)
    else
      false
    end
  end
end
