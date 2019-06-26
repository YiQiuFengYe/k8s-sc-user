def label = "slave-${UUID.randomUUID().toString()}"

def helmLint(String chartDir) {
  println "校验 chart 模板"
  sh "helm lint ${chartDir}"
}

def helmInit() {
  println "初始化 helm client"
  sh "helm init --client-only --stable-repo-url https://mirror.azure.cn/kubernetes/charts/"
}

def helmRepo(Map args) {
  println "添加 k8s-sc repo"
  sh "helm repo add --username ${args.username} --password ${args.password} k8s-sc http://192.168.4.112/chartrepo/k8s-sc"

  println "更新 repo"
  sh "helm repo update"

  println "获取 Chart 包"
  sh """
    helm fetch k8s-sc/k8s-sc
    tar -xzvf k8s-sc-0.1.0.tgz
    """
}

def helmDeploy(Map args) {
  helmInit()
  helmRepo(args)

  if (args.dry_run) {
    println "Debug 应用"
    sh """
      helm upgrade \
    --dry-run \
    --debug \
    --install ${args.name} ${args.chartDir} \
    --set service_user.image.repository=${args.image} \
    --set service_user.image.tag=${args.tag} \
    --set imagePullSecrets[0].name=smile \
    --namespace=${args.namespace}
    """
  } else {
    println "部署应用"
    sh """
      helm upgrade \
    --install ${args.name} ${args.chartDir} \
    --set service_user.image.repository=${args.image} \
    --set service_user.image.tag=${args.tag} \
    --namespace=${args.namespace}
      """
    echo "应用 ${args.name} 部署成功. 可以使用 helm status ${args.name} 查看应用状态"
  }
}

podTemplate(label: label, containers: [
        containerTemplate(name: 'maven', image: 'maven:3.6-alpine', command: 'cat', ttyEnabled: true),
        containerTemplate(name: 'docker', image: 'docker', command: 'cat', ttyEnabled: true),
        containerTemplate(name: 'helm', image: 'alpine/helm:latest', command: 'cat', ttyEnabled: true)
], volumes: [
        persistentVolumeClaim(mountPath: '/root/.m2', claimName: 'maven-pvc', readOnly: 'false'),
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
    stage('运行 Helm') {
      withCredentials([[$class: 'UsernamePasswordMultiBinding',
                        credentialsId: 'harbor',
                        usernameVariable: 'harborUser',
                        passwordVariable: 'harborPassword']]) {
        container('helm') {
          // todo,可以做分支判断
          echo "4. [INFO] 开始 Helm 部署"
          helmDeploy(
                  dry_run     : false,
                  name        : "k8s-sc",
                  chartDir    : "k8s-sc",
                  namespace   : "dev",
                  tag         : "${imageTag}",
                  image       : "${image}",
                  username    : "${harborUser}",
                  password    : "${harborPassword}"
          )
          echo "[INFO] Helm 部署应用成功..."
        }
      }
    }
  }
}