def gurus = [
    1. [kubeENV: "dev", envNAME: "DEV" ]
]

gurus.collect { guru ->
    mapJenkinsJobs.collect { map ->
        pipelineJob("${guru) {
            logRotator {
                numToKeep(15)
            }
            configure { xml ->
                        xml / 'properties' / 'hudson.model.ParametersDefinitionProperty' / parameterDefinitions << 'com.cwctravel.hudson.plugins.extended__choice__parameter.ExtendedChoiceParameterDefinition'(plugin: 'extended-choice-parameter@0.76') {
                            name('last_tag')
                            description('Last successful version installed on environment for Release Note generation')
                            quoteValue(false)
                            visibleItemCount(1)
                            type('PT_SINGLE_SELECT')
                            groovyScript('''\
                        import jenkins.model.*

                        def url = Thread.currentThread().toString().tokenize(' ')[2]
                        def jobName = (url =~ /(?<=job\\/)(.*?)(?=\\/)/).collect{ it[1] }.join('/')
                        def job = Jenkins.getInstance().getItemByFullName(jobName)

                        def last_versions = job.builds.findAll{
                        it.result == hudson.model.Result.SUCCESS
                        }.collect{
                        it.buildVariableResolver.resolve("source")
                        }
                        last_versions.add("")
                        last_versions.unique()
                        last_versions.removeElement(null)
                        last_versions.removeElement("develop")

                        return last_versions.join(',')
                        ''')
                            multiSelectDelimiter(',')
                        }
                        }

                        configure { xml ->
                        xml / 'properties' / 'hudson.model.ParametersDefinitionProperty' / parameterDefinitions << 'com.cwctravel.hudson.plugins.extended__choice__parameter.ExtendedChoiceParameterDefinition'(plugin: 'extended-choice-parameter@0.76') {
                            name('notify_on_success')
                            description('Select additional e-mail recipients')
                            quoteValue(false)
                            visibleItemCount(10)
                            type('PT_CHECKBOX')
                            value(config.get('notify', []).join(","))
                            defaultValue(config.get('notifyDefault', []).join(","))
                            multiSelectDelimiter(',')
                        }
                        }

                }

            }
    }