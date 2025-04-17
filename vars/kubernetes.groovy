 // vars/kubernetes.groovy

def getKubeconfigFromServiceAccount(namespace = "jenkins", saName = "jenkins") {
    echo "🔐 Fetching token and CA for ServiceAccount '${saName}' in namespace '${namespace}'..."

    def secretName = sh(
        script: "kubectl -n ${namespace} get sa ${saName} -o jsonpath='{.secrets[0].name}'",
        returnStdout: true
    ).trim()

    def token = sh(
        script: "kubectl -n ${namespace} get secret ${secretName} -o jsonpath='{.data.token}' | base64 -d",
        returnStdout: true
    ).trim()

    def ca = sh(
        script: "kubectl -n ${namespace} get secret ${secretName} -o jsonpath=\"{.data['ca\\.crt']}\"",
        returnStdout: true
    ).trim()

    def server = "https://kubernetes.default.svc"
    def clusterName = "in-cluster"
    def userName = saName
    def contextName = "${saName}-context"

    echo "✅ Building kubeconfig dynamically..."

    return """\
apiVersion: v1
kind: Config
clusters:
- name: ${clusterName}
  cluster:
    server: ${server}
    certificate-authority-data: ${ca}
contexts:
- name: ${contextName}
  context:
    cluster: ${clusterName}
    user: ${userName}
current-context: ${contextName}
users:
- name: ${userName}
  user:
    token: ${token}
"""
}

return this