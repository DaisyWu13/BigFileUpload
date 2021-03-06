// common variables
var iBytesUploaded = 0;
var iBytesTotal = 0;
var iPreviousBytesLoaded = 0;
var iMaxFilesize = 52428800; // 50MB
//var blockSize = 5242880; //5M
var blockSize = 1048576; //1M
var oTimer = 0;
var sResultFileSize = '';
var oFile = null;
var fileBlockNum = 0;
var currentBlockNum = 0;

function secondsToTime(secs) { // we will use this function to convert seconds in normal time format
    var hr = Math.floor(secs / 3600);
    var min = Math.floor((secs - (hr * 3600))/60);
    var sec = Math.floor(secs - (hr * 3600) -  (min * 60));

    if (hr < 10) {hr = "0" + hr; }
    if (min < 10) {min = "0" + min;}
    if (sec < 10) {sec = "0" + sec;}
    if (hr) {hr = "00";}
    return hr + ':' + min + ':' + sec;
};

function bytesToSize(bytes) {
    var sizes = ['Bytes', 'KB', 'MB'];
    if (bytes == 0) return 'n/a';
    var i = parseInt(Math.floor(Math.log(bytes) / Math.log(1024)));
    return (bytes / Math.pow(1024, i)).toFixed(1) + ' ' + sizes[i];
};

function fileSelected() {

    // hide different warnings
    document.getElementById('upload_response').style.display = 'none';
 
    document.getElementById('error2').style.display = 'none';
    document.getElementById('abort').style.display = 'none';
    document.getElementById('warnsize').style.display = 'none';

    // get selected file element
    oFile = document.getElementById('image_file').files[0];

    // filter for image files
//    var rFilter = /^(image\/bmp|image\/gif|image\/jpeg|image\/png|image\/tiff)$/i;
    var rFilter = /^image./i;
//    if (! rFilter.test(oFile.type)) {
//        return;
//    }

    // little test for filesize
    if (oFile.size > iMaxFilesize) {
        document.getElementById('warnsize').style.display = 'block';
        return;
    }

    //compute the block number of the file to upload
	fileBlockNum = Math.ceil(oFile.size/blockSize);
    // get preview element
    var oImage = document.getElementById('preview');

    // prepare HTML5 FileReader
    var oReader = new FileReader();
        oReader.onload = function(e){

        // e.target.result contains the DataURL which we will use as a source of the image
        oImage.src = e.target.result;

        oImage.onload = function () { // binding onload event

            // we are going to display some custom image information here
            sResultFileSize = bytesToSize(oFile.size);
            document.getElementById('fileinfo').style.display = 'block';
            document.getElementById('filename').innerHTML = 'Name: ' + oFile.name;
            document.getElementById('filesize').innerHTML = 'Size: ' + sResultFileSize;
            document.getElementById('filetype').innerHTML = 'Type: ' + oFile.type;
            document.getElementById('filedim').innerHTML = 'Dimension: ' + oImage.naturalWidth + ' x ' + oImage.naturalHeight;
        };
    };

    // read selected file as DataURL
    oReader.readAsDataURL(oFile);
}

//click button to upload
function startUploading() {
    // cleanup all temp states
    iPreviousBytesLoaded = 0;
    document.getElementById('upload_response').style.display = 'none';
  
    document.getElementById('error2').style.display = 'none';
    document.getElementById('abort').style.display = 'none';
    document.getElementById('warnsize').style.display = 'none';
    document.getElementById('progress_percent').innerHTML = '';
    var oProgress = document.getElementById('progress');
    oProgress.style.display = 'block';
    oProgress.style.width = '0px';

    // get form data for POSTing
    //var vFD = document.getElementById('upload_form').getFormData(); // for FF3
    uploadBlock();

    // set inner timer
    oTimer = setInterval(doInnerUpdates, 300);
}

function uploadBlock(){
	 var start = blockSize*currentBlockNum;
	 var end = ( blockSize*(currentBlockNum+1) < oFile.size)? blockSize*(currentBlockNum+1):oFile.size;
	 var formData = new FormData();
	
	 formData.append("totalBlocks", fileBlockNum);
	 formData.append("currentBlockNum",currentBlockNum);
	 formData.append("fileType", oFile.type);
	 formData.append("fileName", encodeURI(oFile.name));
	 formData.append("file", oFile.slice(start, end));
	 var oXHR = new XMLHttpRequest();        
	    oXHR.upload.addEventListener('progress', uploadProgress, false);
	    oXHR.addEventListener('load', uploadFinish, false);
	    oXHR.addEventListener('error', uploadError, false);
	    oXHR.addEventListener('abort', uploadAbort, false);
//	    oXHR.setRequestHeader("fileType", oFile.type);
	    oXHR.open('POST', '/BigFileUpload/uploadFile'); 
	    oXHR.send(formData);
}

function doInnerUpdates() { // we will use this function to display upload speed
    var iCB = iBytesUploaded;
    var iDiff = iCB - iPreviousBytesLoaded;

    // if nothing new loaded - exit
    if (iDiff == 0)
        return;

    iPreviousBytesLoaded = iCB;
    iDiff = iDiff * 2;
    var iBytesRem = iBytesTotal - iPreviousBytesLoaded;
    var secondsRemaining = iBytesRem / iDiff;

    // update speed info
    var iSpeed = iDiff.toString() + 'B/s';
    if (iDiff > 1024 * 1024) {
        iSpeed = (Math.round(iDiff * 100/(1024*1024))/100).toString() + 'MB/s';
    } else if (iDiff > 1024) {
        iSpeed =  (Math.round(iDiff * 100/1024)/100).toString() + 'KB/s';
    }

    document.getElementById('speed').innerHTML = iSpeed;
    document.getElementById('remaining').innerHTML = '| ' + secondsToTime(secondsRemaining);        
}

function uploadProgress(e) { // upload process in progress
    if (e.lengthComputable) {
        iBytesUploaded = e.loaded;
        iBytesTotal = e.total;
        var iPercentComplete = Math.round(e.loaded * 100 / e.total);
        var iBytesTransfered = bytesToSize(iBytesUploaded);

        document.getElementById('progress_percent').innerHTML = iPercentComplete.toString() + '%';
        document.getElementById('progress').style.width = (iPercentComplete * 4).toString() + 'px';
        document.getElementById('b_transfered').innerHTML = iBytesTransfered;
        if (iPercentComplete == 100) {
            var oUploadResponse = document.getElementById('upload_response');
            oUploadResponse.innerHTML = '<h1>Please wait...processing</h1>';
            oUploadResponse.style.display = 'block';
        }
    } else {
        document.getElementById('progress').innerHTML = 'unable to compute';
    }
}

function uploadFinish(e) { // upload successfully finished
    var oUploadResponse = document.getElementById('upload_response');
    oUploadResponse.innerHTML = e.target.responseText;
    oUploadResponse.style.display = 'block';
	currentBlockNum++;
    if( currentBlockNum < fileBlockNum)
    {
    	uploadBlock();
    }else
    {
    	document.getElementById('progress_percent').innerHTML = '100%';
    	document.getElementById('progress').style.width = '400px';
    	document.getElementById('filesize').innerHTML = sResultFileSize;
    	document.getElementById('remaining').innerHTML = '| 00:00:00';

    	clearInterval(oTimer);
    }
    

}

function uploadError(e) { // upload error
    document.getElementById('error2').style.display = 'block';
    clearInterval(oTimer);
}  

function uploadAbort(e) { // upload abort
    document.getElementById('abort').style.display = 'block';
    clearInterval(oTimer);
}
