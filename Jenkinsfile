node {
    stage 'Build'
    checkout scm
    sh 'gradle clean build'

    stage 'Assemble'
    sh 'gradle assemble'
}
