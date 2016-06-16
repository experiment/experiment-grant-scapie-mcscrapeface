class GrantsController < ApplicationController
    def index
        @grants = Grant.all
    end
end
