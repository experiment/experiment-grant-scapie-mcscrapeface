require 'rails_helper'
require 'spec_helper'

RSpec.configure do |config|
  config.include Capybara::DSL
end

RSpec.describe "UserPages", type: :request do

  subject { page }

  # describe "signup page" do

  #   it 'basic selector check' do 
  #     get signup_path

  #     { should have_selector('h1',    text: 'Signup!') }
  # end
  describe "signup" do

    # let(:submit) { "Create my account" }

    describe "with invalid information" do
      it "should create a user" do
        visit '/signup'
        fill_in "user[email]",                 :with => ""
        fill_in "Name",                  :with => "jeff"
        fill_in "Password",              :with => "ilovegrapes"
        fill_in "user[password_confirmation]", :with => "ilovegrapes"

        # post users_path, name: "Example User", email: "user@example.com", password: 'foobar', password_confirmation: 'foobar'
        expect { click_button "Create my account" }.not_to change(User, :count)
      end
    end

    describe "with valid information" do
      it "should create a user" do
        visit '/signup'
        fill_in "user[email]",                 :with => "alindeman@example.com"
        fill_in "Name",                  :with => "jeff"
        fill_in "Password",              :with => "ilovegrapes"
        fill_in "user[password_confirmation]", :with => "ilovegrapes"

        # post users_path, name: "Example User", email: "user@example.com", password: 'foobar', password_confirmation: 'foobar'
        expect { click_button "Create my account" }.to change(User, :count).by(1)
      end
    end
  end
end
