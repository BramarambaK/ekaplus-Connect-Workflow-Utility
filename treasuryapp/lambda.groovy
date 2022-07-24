 node {
   def app
   def version;
   def build_stage = "${TEMPLATE_FILE_NAME}"
   echo "Template file is -> ${TEMPLATE_FILE_NAME}"
   def build_stage_= build_stage.split('-')[0]
   def env_properties_path = 'Connect-Workflow-Utility/treasuryapp/'
   def tag = 'test';
   def remote = [: ]

   // remote.name = "Review Server"
   // remote.host = "172.16.5.101"
   // remote.allowAnyHosts = true
   // remote.user="trmuser"
   // remote.password="trmuser"

  //  step([$class: 'WsCleanup'])

   stage('CTRM-ML-Checkout') {
     dir('CTRM-ML') {
       git branch: '${CTRM_ML_BRANCH}', credentialsId: 'rajeshkscredentials', url: "https://github.com/ekaplus/CTRM-ML.git"
     }
   }

   stage('Pricing-Checkout') {
     dir('Pricing') {
       git branch: '${PRICING_BRANCH}', credentialsId: 'rajeshkscredentials', url: "https://github.com/ekaplus/Pricing.git"
     }
   }

   stage('Power-Checkout') {
     dir('PowerApplication') {
       git branch: '${POWER_BRANCH}', credentialsId: 'rajeshkscredentials', url: "https://github.com/ekaplus/PowerApplication.git"
     }
   }

   stage('Connect-Workflow-Utility-Checkout') {
     dir('Connect-Workflow-Utility') {
       git branch: '${TREASURY_BRANCH}', credentialsId: 'rajeshkscredentials', url: "https://github.com/ekaplus/Connect-Workflow-Utility.git"
     }
   }

   stage('Clean Build Folder') {
     dir('build') {
       deleteDir()
     }
   }


   stage('Get Version and Build Tag') {
     if (isUnix()) {
       dir("${env_properties_path}") {
         version = sh returnStdout: true, script: "grep -iR '^version' environment.properties | awk -F = '{print \$2 }'"
         version = version.replaceAll("[\r\n]+", "");
         echo "version is   -> ${version}"
        tag = "build_${build_stage_}";
         echo "Final Tag Name is   -> ${tag}"

       }

     }
   }

   stage('Copy All Lambda Function code to the build folder.') {
     echo "copying the recommendation code to lambda."

     fileOperations([folderCopyOperation(destinationFolderPath: 'build/TrainRecommendationForAnUserFunction', sourceFolderPath: 'CTRM-ML/ml-recommendation/aws-lambda/code/')])
     fileOperations([folderCopyOperation(destinationFolderPath: 'build/TrainRecommendationForAllUsersFunction', sourceFolderPath: 'CTRM-ML/ml-recommendation/aws-lambda/code/')])
     fileOperations([folderCopyOperation(destinationFolderPath: 'build/TrainRecommendationFunction', sourceFolderPath: 'CTRM-ML/ml-recommendation/aws-lambda/code/')])

     echo "copying the NLP code to lambda build folder."
     fileOperations([folderCopyOperation(destinationFolderPath: 'build/TrainNlpFunction', sourceFolderPath: 'CTRM-ML/nlp-new/aws-lambda/code/')])
     fileOperations([folderCopyOperation(destinationFolderPath: 'build/ProcessSentenceFunction', sourceFolderPath: 'CTRM-ML/nlp-new/aws-lambda/code/')])
     fileOperations([folderCopyOperation(destinationFolderPath: 'build/ResetTrainingFunction', sourceFolderPath: 'CTRM-ML/nlp-new/aws-lambda/code/')])
     fileOperations([folderCopyOperation(destinationFolderPath: 'build/TagTextFunction', sourceFolderPath: 'CTRM-ML//generic-nlp/code/')])

     echo "copying the Anomaly code to lambda build folder."
     fileOperations([folderCopyOperation(destinationFolderPath: 'build/CheckAnomalyFunction', sourceFolderPath: 'CTRM-ML/anomaly-detection/aws-lambda/code/')])
     fileOperations([folderCopyOperation(destinationFolderPath: 'build/TrainAnomalyDetectionFunction', sourceFolderPath: 'CTRM-ML/anomaly-detection/aws-lambda/code/')])

     echo "copying the User Activity code to lambda build folder."
     fileOperations([folderCopyOperation(destinationFolderPath: 'build/UserActivityAnalysis', sourceFolderPath: 'CTRM-ML/user-activity-modeling/code/')])

     echo "copying the Treasury code to lambda build folder."
     fileOperations([folderCopyOperation(destinationFolderPath: 'build/TreasuryFunction', sourceFolderPath: 'Connect-Workflow-Utility/treasuryapp/code/')])
     fileOperations([folderCopyOperation(destinationFolderPath: 'build/TreasuryPreProcessing', sourceFolderPath: 'Connect-Workflow-Utility/treasuryapp/code/')])

     echo "copying the Pricing code to lambda build folder."
     fileOperations([folderCopyOperation(destinationFolderPath: 'build/PricingLambdaFunction', sourceFolderPath: 'Pricing/Pricing - python/')])

     echo "copying the Power code to lambda build folder."
     fileOperations([folderCopyOperation(destinationFolderPath: 'build/DeliveryItemPriceFunction', sourceFolderPath: 'PowerApplication/aws-lambda/functions/')])
     fileOperations([folderCopyOperation(destinationFolderPath: 'build/DeliveryItemValuationFunction', sourceFolderPath: 'PowerApplication/aws-lambda/functions/')])
     fileOperations([folderCopyOperation(destinationFolderPath: 'build/GetMarketPriceFunction', sourceFolderPath: 'PowerApplication/aws-lambda/functions/')])

     echo "copying the template file inside the lambda build folder."
     echo "build/${TEMPLATE_FILE_NAME}"
     fileOperations([fileCopyOperation(excludes: '', flattenFiles: true, includes: "Connect-Workflow-Utility/treasuryapp/lambda-template/${TEMPLATE_FILE_NAME}", targetLocation: 'build/')])
     fileOperations([fileRenameOperation(destination: 'build/template.yaml', source: "build/${TEMPLATE_FILE_NAME}")])

   }

   stage('Zip the build folder.') {
     sh '''
     zip -r build.zip build 
     '''
   }

   stage('Push the build to nexus') {

     sh """
     curl -v -u admin:admin123 --upload-file build.zip http://192.168.1.94:8081/nexus/content/repositories/cac/com/eka/connect/lambda-build/${version}/${tag}.zip
    """
   }
 }

    //Lambda functions
  //  def recommendation_functions = ['TrainRecommendationForAnUserFunction': '/ml-recommendation/aws-lambda/code/', 'TrainRecommendationForAllUsersFunction': '/ml-recommendation/aws-lambda/code/', 'TrainRecommendationFunction': '/ml-recommendation/aws-lambda/code/'];
  //  def nlp_functions = ['TrainNlpFunction': '/nlp-new/aws-lambda/code/', 'ProcessSentenceFunction': '/nlp-new/aws-lambda/code/', 'ResetTrainingFunction': '/nlp-new/aws-lambda/code/'];
  //  def generic_nlp_function = ['TagTextFunction': '/generic-nlp/code/'];
  //  def anomaly_functions = ['CheckAnomalyFunction': '/anomaly-detection/aws-lambda/code/', 'TrainAnomalyDetectionFunction': '/anomaly-detection/aws-lambda/code/'];
  //  def user_activity_functions = ['UserActivityAnalysis': '/user-activity-modeling/code/'];
  //  def power_functions = ['DeliveryItemPriceFunction': '/aws-lambda/functions/', 'DeliveryItemValuationFunction': '/aws-lambda/functions/', 'GetMarketPriceFunction': '/aws-lambda/functions/'];
  //  def treasury_functions = ['TreasuryFunction': '/treasuryapp/code/', 'TreasuryPreProcessing': '/treasuryapp/code/']
  //  def pricing_functions = ['PricingLambdaFunction': '/Pricing%20-%20python/']

  //  // Repositories map to functions
  //  //    def function_map_list = ['CTRM-ML':recommendation_functions, 'CTRM-ML':nlp_functions, 'CTRM-ML':generic_nlp_function, 'CTRM-ML':anomaly_functions, 'CTRM-ML':user_activity_functions, 'PowerApplication':power_functions, 'Connect-Workflow-Utility':treasury_functions, 'Pricing':pricing_functions]
  //  def function_map_list = ['CTRM-ML': [recommendation_functions, nlp_functions, generic_nlp_function, anomaly_functions, user_activity_functions], 'PowerApplication': [power_functions], 'Connect-Workflow-Utility': [treasury_functions], 'Pricing': [pricing_functions]]

   // stage('Copy Lambda function Code to Function Name Folders'){
   //     for (function_map in function_map_list) {
   //         key_ = function_map.key
   //         for (maps in  function_map.value) {
   //             for (map_ in maps.value) {
   //                     // fileOperations([folderCreateOperation('build/' + map_.key)])
   //                     println map_.key
   //                     println map_.value

   //                     // sh 'ls -l'
   //                     dir ('build/' + map_.key) {
   //                         deleteDir()
   //                     }
   //                     fileOperations([folderCopyOperation(destinationFolderPath: 'build/' + map_.key, sourceFolderPath: key_ + map_.value)])
   //                     echo 'build/' + map_.key

   //                 }

   //             }
   //         }
   // }

   // for (entry in map) {
   //     println "Hex Code: $entry.key = Color Name: $entry.value"
   // }