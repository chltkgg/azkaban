/*
 * Copyright 2012 LinkedIn Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package azkaban.project;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import azkaban.flow.Flow;
import azkaban.project.ProjectLogEvent.EventType;
import azkaban.user.Permission;
import azkaban.user.User;
import azkaban.utils.Props;
import azkaban.utils.Triple;

public interface ProjectLoader {

  /**
   * Returns all projects which are active
   *
   * @return
   * @throws ProjectManagerException
   */
  List<Project> fetchAllActiveProjects() throws ProjectManagerException;

  /**
   * Loads whole project, including permissions, by the project id.
   *
   * @param id
   * @return
   * @throws ProjectManagerException
   */
  Project fetchProjectById(int id) throws ProjectManagerException;

  /**
   * Loads whole project, including permissions, by the project name.
   * @param name
   * @return
   * @throws ProjectManagerException
   */
  Project fetchProjectByName(String name) throws ProjectManagerException;

  /**
   * Should create an empty project with the given name and user and adds it to
   * the data store. It will auto assign a unique id for this project if
   * successful.
   *
   * If an active project of the same name exists, it will throw an exception.
   * If the name and description of the project exceeds the store's constraints,
   * it will throw an exception.
   *
   * @param name
   * @return
   * @throws ProjectManagerException if an active project of the same name
   *           exists.
   */
  Project createNewProject(String name, String description, User creator)
      throws ProjectManagerException;

  /**
   * Removes the project by marking it inactive.
   *
   * @param project
   * @throws ProjectManagerException
   */
  void removeProject(Project project, String user)
      throws ProjectManagerException;

  /**
   * Adds and updates the user permissions. Does not check if the user is valid.
   * If the permission doesn't exist, it adds. If the permission exists, it
   * updates.
   *
   * @param project
   * @param name
   * @param perm
   * @param isGroup
   * @throws ProjectManagerException
   */
  void updatePermission(Project project, String name, Permission perm,
      boolean isGroup) throws ProjectManagerException;

  void removePermission(Project project, String name, boolean isGroup)
      throws ProjectManagerException;

  /**
   * Modifies and commits the project description.
   *
   * @param project
   * @param description
   * @throws ProjectManagerException
   */
  void updateDescription(Project project, String description, String user)
      throws ProjectManagerException;

  /**
   * Stores logs for a particular project. Will soft fail rather than throw
   * exception.
   *
   * @param project
   * @param type
   * @param message return true if the posting was success.
   */
  boolean postEvent(Project project, EventType type, String user,
      String message);

  /**
   * Returns all the events for a project sorted
   *
   * @param project
   * @return
   */
  List<ProjectLogEvent> getProjectEvents(Project project, int num,
      int skip) throws ProjectManagerException;

  /**
   * Will upload the files and return the version number of the file uploaded.
   */
  void uploadProjectFile(int projectId, int version, File localFile, String user)
      throws ProjectManagerException;

  /**
   * Add project and version info to the project_versions table. This current maintains the metadata for each uploaded
   * version of the project
   *
   * @param projectId
   * @param version
   * @param localFile
   * @param uploader
   * @param md5
   * @param resourceId
   * @throws ProjectManagerException
   */
  void addProjectVersion(int projectId, int version, File localFile, String uploader, byte[] md5, String resourceId)
      throws ProjectManagerException;

  /**
   * Fetch project metadata from project_versions table
   *
   * @param projectId project ID
   * @param version version
   * @return ProjectFileHandler object containing the metadata
   */
  ProjectFileHandler fetchProjectMetaData(int projectId, int version);

  /**
   * Get file that's uploaded.
   *
   * @return
   */
  ProjectFileHandler getUploadedFile(int projectId, int version)
      throws ProjectManagerException;

  /**
   * Changes and commits different project version.
   *
   * @param project
   * @param version
   * @throws ProjectManagerException
   */
  void changeProjectVersion(Project project, int version, String user)
      throws ProjectManagerException;

  void updateFlow(Project project, int version, Flow flow)
      throws ProjectManagerException;

  /**
   * Uploads all computed flows
   *
   * @param project
   * @param version
   * @param flows
   * @throws ProjectManagerException
   */
  void uploadFlows(Project project, int version, Collection<Flow> flows)
      throws ProjectManagerException;

  /**
   * Upload just one flow.
   *
   * @param project
   * @param version
   * @param flow
   * @throws ProjectManagerException
   */
  void uploadFlow(Project project, int version, Flow flow)
      throws ProjectManagerException;

  /**
   * Fetches one particular flow.
   *
   * @param project
   * @param version
   * @param flowId
   * @throws ProjectManagerException
   */
  Flow fetchFlow(Project project, String flowId)
      throws ProjectManagerException;

  /**
   * Fetches all flows.
   *
   * @param project
   * @param version
   * @param flowId
   * @throws ProjectManagerException
   */
  List<Flow> fetchAllProjectFlows(Project project)
      throws ProjectManagerException;

  /**
   * Gets the latest upload version.
   */
  int getLatestProjectVersion(Project project)
      throws ProjectManagerException;

  /**
   * Upload Project properties
   *
   * @param project
   * @param path
   * @param properties
   * @throws ProjectManagerException
   */
  void uploadProjectProperty(Project project, Props props)
      throws ProjectManagerException;

  /**
   * Upload Project properties. Map contains key value of path and properties
   *
   * @param project
   * @param path
   * @param properties
   * @throws ProjectManagerException
   */
  void uploadProjectProperties(Project project, List<Props> properties)
      throws ProjectManagerException;

  /**
   * Fetch project properties
   *
   * @param project
   * @param propsName
   * @return
   * @throws ProjectManagerException
   */
  Props fetchProjectProperty(Project project, String propsName)
      throws ProjectManagerException;

  /**
   * Fetch all project properties
   *
   * @param project
   * @return
   * @throws ProjectManagerException
   */
  Map<String, Props> fetchProjectProperties(int projectId, int version)
      throws ProjectManagerException;

  /**
   * Cleans all project versions less tha
   *
   * @param projectId
   * @param version
   * @throws ProjectManagerException
   */
  void cleanOlderProjectVersion(int projectId, int version)
      throws ProjectManagerException;

  void updateProjectProperty(Project project, Props props)
      throws ProjectManagerException;

  Props fetchProjectProperty(int projectId, int projectVer, String propsName)
      throws ProjectManagerException;

  List<Triple<String, Boolean, Permission>> getProjectPermissions(int projectId)
      throws ProjectManagerException;

  void updateProjectSettings(Project project) throws ProjectManagerException;
}
