module ApplicationHelper
  def check_params_for_funder(params, funder)
    if params[:funder_by_id]
      params[:funder_by_id].include?(funder.id.to_s)
    else
      false
    end
  end

  def sortable_grants(column, title=nil, **kwargs)
    title ||= column.titleize
    direction = (column == sort_column && sort_direction == "asc") ? "desc" : "asc"
    if column == sort_column
      if sort_direction == "asc"
        icon = "<i class='material-icons grant-icon'>keyboard_arrow_up</i>"
      else
        icon = "<i class='material-icons grant-icon'>keyboard_arrow_down</i>"
      end
    else
      icon = ""
    end
    link_to(title, :sort => column, :direction => direction, **kwargs) + icon.html_safe

  end

  def other_params(params)
    params_to_hash(params.slice(:q, :funder_by_id))
  end

  private
    def params_to_hash(params)
      hash = params.map { |k,v| [k.split('.').reverse,v] }.map { |keys,val| keys.inject(val) { |val, e| { e => val }} }.inject({}) { |hsh, h| hsh.deep_merge(h) }
      Hash[hash.map{ |k, v| [k.to_sym, v] }]
    end

end
