def label = "slave-${UUID.randomUUID().toString()}"

podTemplate(label: label, containers: [
        containerTemplate(name: 'maven', image: 'maven:3.6-alpine', command: 'cat', ttyEnabled: true),
        containerTemplate(name: 'docker', image: 'docker', command: 'cat', ttyEnabled: true),
        containerTemplate(name: 'kubectl', image: 'cnych/kubectl', command: 'cat', ttyEnabled: true),
        containerTemplate(name: 'helm', image: 'alpine/helm:latest', command: 'cat', ttyEnabled: true)
], volumes: [
        persistentVolumeClaim(mountPath: '/root/.m2', claimName: 'maven-pvc', readOnly: 'false'),
        hostPathVolume(mountPath: '/home/jenkins/.kube', hostPath: '/root/.kube'),
        hostPathVolume(mountPath: '/var/run/docker.sock', hostPath: '/var/run/docker.sock')
]) {
  node(label) {
    def myRepo = checkout scm
    def gitCommit = myRepo.GIT_COMMIT
    def gitBranch = myRepo.GIT_BRANCH
    def imageTag = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
    def dockerRegistryUrl = "192.168.4.112"
    def imageEndpoint = "sc/service-user"
    def image = "${dockerRegistryUrl}/${imageEndpoint}"

    stage('单元测试') {
      echo "测试阶段"
    }
    stage('代码编译打包') {
      try {
        container('maven') {
          echo "2. 代码编译打包阶段"
          sh "mvn -f k8s-sc-user-service/pom.xml -s settings.xml clean package -Dmaven.test.skip=true"
        }
      } catch (exc) {
        println "构建失败 - ${currentBuild.fullDisplayName}"
        throw(exc)
      }
    }
    container('构建 Docker 镜像') {
      withCredentials([[$class: 'UsernamePasswordMultiBinding',
                        credentialsId: 'harbor',
                        usernameVariable: 'harborUser',
                        passwordVariable: 'harborPassword']]) {
        container('docker') {
          echo "3. 构建 Docker 镜像阶段"
          sh """
              docker login -u ${harborUser} -p ${harborPassword} ${dockerRegistryUrl}
              docker build --add-host nexus.smile.com:192.168.4.113 -t ${image}:${imageTag} .
              docker push ${image}:${imageTag}
              """
        }
      }
    }
    stage('运行 Kubectl') {
      container('kubectl') {
        echo "查看 K8S 集群 Pod 列表"
        sh "kubectl get pods -n dev"
        sh """
          sed -i "s#<IMAGE>#${image}#g" mainfests/k8s.yaml
          sed -i "s#<IMAGE_TAG>#${imageTag}#g" mainfests/k8s.yaml
          kubectl apply -f mainfests/k8s.yaml --record
        """
      }
    }

  }
}