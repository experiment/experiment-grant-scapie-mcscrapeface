module GrantsHelper
  include ActionView::Helpers::AssetTagHelper

  def image_src(grant)
    if grant.organization == 'Social Science Research Council'
        tag = image_url('ssrc_org.jpg')
    elsif grant.organization == 'Institute of Education Sciences'
        tag = image_url('ies.jpg')
    elsif grant.organization == 'Spencer Foundation'
        tag = image_url('spencer_foundation.jpg')
    else
        tag = image_url('grant.jpg')
    end
    tag
  end

  def desc_too_long(grant)
    '''
    return true if length of description is greater than 1149
    '''
    grant.data['description'].length >= 1150
  end

  def shorter_desc(grant)
    '''
    return first 1149 chars of desc + ...
    '''
    grant.data['description'][0..1049] + '...'
  end


end
