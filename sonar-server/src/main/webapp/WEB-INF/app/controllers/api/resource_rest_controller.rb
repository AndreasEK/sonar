#
# Sonar, entreprise quality control tool.
# Copyright (C) 2008-2012 SonarSource
# mailto:contact AT sonarsource DOT com
#
# Sonar is free software; you can redistribute it and/or
# modify it under the terms of the GNU Lesser General Public
# License as published by the Free Software Foundation; either
# version 3 of the License, or (at your option) any later version.
#
# Sonar is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
# Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public
# License along with Sonar; if not, write to the Free Software
# Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
#
class Api::ResourceRestController < Api::RestController
  
  before_filter :load_resource
  
  def load_resource
    resource_id=params[:resource]
    if resource_id
      @resource=Project.by_key(resource_id)
      if @resource.nil?
        rest_status_ko("Resource [#{resource_id}] not found", 404)
        return
      end
      access_denied unless is_user?(@resource)
    end
  end
 
end