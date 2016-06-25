require 'rails_helper'
require 'spec_helper'

# RSpec.describe User, type: :model do
#   pending "add some examples to (or delete) #{__FILE__}"
# end

describe User do

  # before do
  #   @user = User.new(name: "Example User", email: "user@example.com")
  # end

  # subject { @user }

  # it { should respond_to(:name) }
  # it { should respond_to(:email) }

  # it { should be_valid }

  let(:user) { User.new(name: "Example User", email: "user@example.com",
                        password: "foobar", password_confirmation: "foobar") }


  describe "responds to attrs" do
    it "should respond to attrs" do
        expect(user).to respond_to(:name)
        expect(user).to respond_to(:email)
        expect(user).to respond_to(:password_digest)
        expect(user).to be_valid
    end
  end


  describe 'name' do

    context 'when name isnt present' do
      before { user.name = "" }
      it "should be invalid when name is not present" do
        expect(user).to_not be_valid
      end
    end

    context 'when name is too long' do
      before { user.name = "a" * 51 }
      it "should be invalid" do
        expect(user).to_not be_valid
      end
    end

  end

  describe 'email' do

    context 'when email isnt present' do
      before { user.email = "" }
      it "should be invalid" do
        expect(user).to_not be_valid
      end
    end

    context 'when email isnt formatted properly' do
      addresses = %w[user@foo,com user_at_foo.org example.user@foo.
                      foo@bar_baz.com foo@bar+baz.com]
      addresses.each do |inv|
        before { user.email = inv }
        it "should be invalid" do
          expect(user).to_not be_valid
        end            
      end
    end

    context "when email is formatted proper" do
      addresses = %w[user@foo.COM A_US-ER@f.b.org frst.lst@foo.jp a+b@baz.cn]
      addresses.each do |v|
        before { user.email = v }
        it "should be valid" do
          expect(user).to be_valid
        end            
      end
    end

    context "when email is taken" do
      let(:dup_user) { user.dup }
      before { user.save }
      it "should be invalid" do
        dup_user.email = user.email.upcase
        expect(dup_user).to_not be_valid
      end
    end
  end

  describe 'password' do

    context "when password is not present" do
      before { user.password = user.password_confirmation = ""}
      it "should not be valid" do
        expect(user).to_not be_valid
      end
    end

    context "when password doesn;t match confirmation" do
      before { user.password_confirmation = 'mismatch' }
      it "should not be valid" do 
        expect(user).to_not be_valid
      end
    end

    context "when password confiramtion is null" do
      before { user.password_confirmation = '' }
      it "should not be valid" do 
        expect(user).to_not be_valid
      end
    end

    context "when password is too shrt" do
      before { user.password = user.password_confirmation = "a" * 5 }
      it "should not be valid" do
        expect(user).to_not be_valid
      end
    end

    context "return value of authenticate method" do
      before { user.save }
      let(:found_user) { User.find_by_email(user.email) }
      let(:user_for_invalid_password) { found_user.authenticate("invalid") }

      it "should be found with valid password" do
        expect(user).to eq found_user.authenticate(user.password)
      end

      it "should not be found with invalid password" do
        expect(user).to_not eq user_for_invalid_password
        expect(user_for_invalid_password).to eq false
      end
    end

  end



#   describe "when email is not present" do
#     before { @user.email = "" }
#     it { should_not be_valid }
#   end

#   describe "when username is too long" do
#     before { @user.name = "a" * 51 }
#     it { should_not be_valid }
#   end

#   describe "when email format is invalid" do
#     it "should be invalid" do
      # addresses = %w[user@foo,com user_at_foo.org example.user@foo.
      #                foo@bar_baz.com foo@bar+baz.com]
#       addresses.each do |invalid_address|
#         @user.email = invalid_address
#         @user.should_not be_valid
#       end
#     end
#   end

#   describe "when email format is valid" do
#     it "should be valid" do
#       addresses = %w[user@foo.COM A_US-ER@f.b.org frst.lst@foo.jp a+b@baz.cn]
#       addresses.each do |valid_address|
#         @user.email = valid_address
#         @user.should be_valid
#       end
#     end
#   end

#   describe "when email address is already taken" do
#     before do
#       user_with_same_email = @user.dup
#       user_with_same_email.email = @user.email.upcase
#       user_with_same_email.save
#     end

#     it { should_not be_valid }
#   end


end
