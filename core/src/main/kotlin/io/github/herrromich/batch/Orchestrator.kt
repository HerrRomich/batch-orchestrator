package io.github.herrromich.batch

import java.util.*

/**
 * Batch orchestrator.
 */
interface Orchestrator {

    /**
     * Configured available jobs.
     */
    val jobs: Map<String, Job>

    /**
     * Starts an execution of job.
     *
     * @return an executing job context.
     *
     * @throws OrchestratorException if job is invalid or already executing.
     */
    @Throws(OrchestratorException::class)
    fun execute(jobName: String): JobContext

    companion object {
        private val ORCHESTRATOR_PROVIDER: OrchestratorProvider by lazy {
            ServiceLoader.load(OrchestratorProvider::class.java).run {
                reload()
                try {
                    single()
                } catch (e: NoSuchElementException) {
                    throw OrchestratorException("There are no registered classes of interface \"${OrchestratorProvider::class.java.name}\".")
                } catch (e: IllegalArgumentException) {
                    throw OrchestratorException("There are more then one registered ${OrchestratorProvider::class.java.name} classes.")
                }
            }
        }

        /**
         * Instantiate an orchestrator of a registered type.
         */
        @JvmStatic
        fun instance(jobs: Set<Job>, configuration: OrchestratorConfiguration) =
            ORCHESTRATOR_PROVIDER.provideOrchestrator(jobs, configuration)
    }
}