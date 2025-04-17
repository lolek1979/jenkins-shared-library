def getKubeconfigFromServiceAccount() {
    def namespace = "jenkins"
    def saName = "jenkins"

    def secretName = sh(script: "kubectl -n ${namespace} get sa ${saName} -o jsonpath='{.secrets[0].name}'", returnStdout: true).trim()
    def token = sh(script: "kubectl -n ${namespace} get secret ${secretName} -o jsonpath='{.data.token}' | base64 -d", returnStdout: true).trim()
    def ca = sh(script: "kubectl -n ${namespace} get secret ${secretName} -o jsonpath=\"{.data['ca\\.crt']}\"", returnStdout: true).trim()
    def server = sh(script: "kubectl config view --minify -o jsonpath='{.clusters[0].cluster.server}'", returnStdout: true).trim()

    def clusterName = "in-cluster"
    def userName = "jenkins"
    def contextName = "jenkins-context"

    return generateKubeconfig(token, server, ca, clusterName, userName, contextName)
}