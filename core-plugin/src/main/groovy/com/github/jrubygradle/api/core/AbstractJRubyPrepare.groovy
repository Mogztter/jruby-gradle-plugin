/*
 * Copyright (c) 2014-2019, R. Tyler Croy <rtyler@brokenco.de>,
 *     Schalk Cronje <ysb33r@gmail.com>, Christian Meier, Lookout, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.github.jrubygradle.api.core

import com.github.jrubygradle.api.gems.GemOverwriteAction
import com.github.jrubygradle.api.gems.GemUtils
import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

import static com.github.jrubygradle.api.gems.GemOverwriteAction.SKIP
import static com.github.jrubygradle.api.gems.GemUtils.extractGems
import static com.github.jrubygradle.api.gems.GemUtils.setupJars

/** Abstract base class for building custom tasks for preparing GEMs.
 *
 * @author Schalk W. Cronjé
 * @author R Tyler Croy
 * @author Christian Meier
 *
 * @since 2.0
 */
@CompileStatic
abstract class AbstractJRubyPrepare extends DefaultTask implements JRubyAwareTask {

    protected AbstractJRubyPrepare() {
        outputs.dir({ AbstractJRubyPrepare t -> new File(t.getOutputDir(), 'gems') }.curry(this))
    }

    /** Target directory for GEMs. Extracted GEMs will end up in {@code outputDir + "/gems"}
     */
    @Internal
    File getOutputDir() {
        project.file(this.outputDir)
    }

    /** Sets the output directory
     *
     * @param f Output directory
     */
    void outputDir(Object f) {
        this.outputDir = f
    }

    /** Sets the output directory
     *
     * @param f Output directory
     */
    void setOutputDir(Object f) {
        outputDir = f
    }

    /** All GEMs that have been supplied as dependencies.
     *
     * @return Collection of GEMs.
     */
    @InputFiles
    FileCollection gemsAsFileCollection() {
        return GemUtils.getGems(project.files(this.dependencies))
    }

    @Internal
    final List<Object> dependencies = []

    /** Adds dependencies from the given configuration to be prepared
     *
     * @param f One or more of file, directory, configuration or list of gems.
     */
    @Optional
    void dependencies(Object... f) {
        this.dependencies.addAll(f.toList())
    }

    /** Location of {@code jruby-complete} JAR.
     *
     * @return Path on local filesystem
     */
    @OutputFile
    abstract protected File getJrubyJarLocation()

    @TaskAction
    void exec() {
        File out = getOutputDir()
        File jrubyJar = jrubyJarLocation
        extractGems(project, jrubyJar, gemsAsFileCollection(), out, SKIP)

        dependencies.findAll {
            it instanceof Configuration
        }.each {
            setupJars((Configuration) it, out, GemOverwriteAction.SKIP)
        }
    }

    private Object outputDir = { -> "${project.buildDir}/.gems" }
}

