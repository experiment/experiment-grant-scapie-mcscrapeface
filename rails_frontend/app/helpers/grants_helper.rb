module GrantsHelper
  include ActionView::Helpers::AssetTagHelper

  def image_src(grant)
    if grant.organization == 'Social Science Research Council'
        tag = image_url('ssrc_org.png')
    elsif grant.organization == 'Institute of Education Sciences'
        tag = image_url('ies.png')
    elsif grant.organization == 'Spencer Foundation'
        tag = image_url('spencer_foundation.png')
    else
        tag = image_url('grant.jpg')
    end
    tag
  end  


end
