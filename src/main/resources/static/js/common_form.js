$(document).ready(function () {
    $("#fileImage").change(function () {
        let fileSize = this.files[0].size;

        if (fileSize > 1048576) {
            this.setCustomValidity("File size less than 1Mb");
            this.reportValidity();
        } else {
            this.setCustomValidity("")
            showImageThumbnail(this);
        }
    });
});

let showImageThumbnail = (fileInput) => {
    var file = fileInput.files[0];
    var reader = new FileReader();
    reader.onload = function (e) {
        $("#thumbnail").attr("src", e.target.result);
    }
    reader.readAsDataURL(file);
}

function showErrorModal(message) {
    showModalDialog("Error", message);
}

function showWarningModal(message) {
    showModalDialog("Warning", message);
}

function showModalDialog(title, message) {
    $("#modalTitle").text(title);
    $("#modalBody").text(message);
    $("#modalDialog").modal();
}