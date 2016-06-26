class SessionsController < ApplicationController
  def new
  end

  def create
    user = User.find_by(email: params[:session][:email].downcase)
    if user && user.authenticate(params[:session][:password])
      # Log the user in and redirect to the user's show page.
      log_in user
      remember user
      flash[:success] = 'Logged in. Yay!'
      redirect_to grants_url
    else
      # Create an error message.
      flash.now[:success] = 'Invalid email/password combination'
      render 'new'
    end
  end

  def destroy
    log_out if logged_in?
    flash[:success] = 'Logged out'
    redirect_to grants_url
  end
end
