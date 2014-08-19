package com.lookout.jruby

import org.gradle.api.Project

class JRubyPluginExtension {

    // More details here: <http://rubygems-proxy.torquebox.org/>
    def String gemrepo_url = "http://rubygems-proxy.torquebox.org/releases"

    String defaultVersion = '1.7.13'
    String execVersion = defaultVersion

    JRubyPluginExtension(Project p) {
        project = p
    }

    void setExecVersion(final String newVersion) {
        execVersion = newVersion

        project.tasks.withType(JRubyExec).each { t ->
            t.jrubyVersion = newVersion
        }
    }

    private Project project
}