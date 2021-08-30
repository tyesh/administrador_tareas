const status = ["En espera", "En desarrollo", "En pruebas", "Finalizado"];
const width = ["0", "25", "60", "100"];
const color = ["0", "bg-warning", "bg-info", "bg-success"];
function initPage(){
    $('#search_box').val("");
    $("#nomb_tareat").val("");
    $("#desc_tarea").val("");
    $("#id_tarea").val("");
    $("#estado_tarea").val("");
    $.get("tasks/biggestTask", function( data ) {
        if(data){
            $("#btarea").html(data.name);
        }else{
            $("#btarea").html("No hay tareas");
        }
    });
    $.get("tasks/smallestTask", function( data ) {
        if(data){
            $("#starea").html(data.name);
        }
        else{
            $("#btarea").html("No hay tareas");
        }
    });
    $.get("tasks/activeTodayTasks", function( data ) {
        if(data){
            $("#cant_completadas").html(data);
        }else{
            $("#cant_completadas").html("0");
        }
    });
    $.get("tasks/completeTodayTasks", function( data ) {
        if(data){
            $("#cant_activas").html(data);
        }else{
            $("#cant_activas").html("0");
        }
    });
    $.get("tasks", function( data ) {
        if(data){
            $("#list_tareas").html("");
            var index = 0;
            var desar = 0;
            var pruebas = 0;
            var final = 0;
            for(index = 0; index < data.length; index++){
                var item = data[index];
                var auxHtml = $("#projects").html();
                auxHtml = auxHtml.replace("_ID_", item.id);
                auxHtml = auxHtml.replaceAll("_NAME_", item.name);
                auxHtml = auxHtml.replaceAll("_DESC_", item.description);
                auxHtml = auxHtml.replace("_STATUS_", status[item.status]);
                auxHtml = auxHtml.replace("_COLOR_", color[item.status]);
                auxHtml = auxHtml.replace("_WIDTH_", width[item.status]);
                switch(item.status) {
                    case 1:
                        desar++;
                        break;
                    case 2:
                        pruebas++;
                        break;
                    case 3:
                        final++
                        break;
                } 
                $("#list_tareas").append(auxHtml);
            }
        }
        // Set new default font family and font color to mimic Bootstrap's default styling
        Chart.defaults.global.defaultFontFamily = 'Nunito', '-apple-system,system-ui,BlinkMacSystemFont,"Segoe UI",Roboto,"Helvetica Neue",Arial,sans-serif';
        Chart.defaults.global.defaultFontColor = '#858796';

        // Pie Chart Example
        var ctx = document.getElementById("myPieChart");
        var myPieChart = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: ["Desarrollo", "Pruebas", "Finalizado"],
            datasets: [{
            data: [desar, pruebas, final],
            backgroundColor: ['#4e73df', '#1cc88a', '#36b9cc'],
            hoverBackgroundColor: ['#2e59d9', '#17a673', '#2c9faf'],
            hoverBorderColor: "rgba(234, 236, 244, 1)",
            }],
        },
        options: {
            maintainAspectRatio: false,
            tooltips: {
            backgroundColor: "rgb(255,255,255)",
            bodyFontColor: "#858796",
            borderColor: '#dddfeb',
            borderWidth: 1,
            xPadding: 15,
            yPadding: 15,
            displayColors: false,
            caretPadding: 10,
            },
            legend: {
            display: false
            },
            cutoutPercentage: 80,
        },
        });
    });
}
function editar(id, name, desc){
    $("#estado_container").css("display", "block");
    $("#modal_tarea").modal('show');
    $("#id_tarea").val(id);
    $("#nomb_tareat").val(name);
    $("#desc_tarea").val(desc);
    $("#btn_eliminar").css("display", "block");
    $("#usuario_tarea").css("display", "block");
    return false;
}
function eliminarTodo(estado){
    $.ajax({
        url:"/tasks/TasksByStatus/" + estado,
        type:"DELETE",
        contentType:"application/json; charset=utf-8",
        dataType:"json",
        success: function() {
            initPage();
        },
        error: function(){initPage();}
    })
    return false;
}
function buscarRep(fecha){
    $.ajax({
        url:"github/popular/" + fecha,
        type:"POST",
        contentType:"application/json; charset=utf-8",
        dataType:"json",
        success: function(data){
            if(data){
                data = data.items;
                $("#list_repositorios").html("");
                var index = 0;
                for(index = 0; index < data.length; index++){
                    var item = data[index];
                    var auxHtml = $("#repositorios").html();
                    auxHtml = auxHtml.replace("_NAME_", item.name);
                    auxHtml = auxHtml.replace("_OWNER_", item.owner.login);
                    auxHtml = auxHtml.replace("_AVATAR", item.owner.avatar_url);
                    auxHtml = auxHtml.replace("_ESTRELLAS_", item.stargazers_count);
                    auxHtml = auxHtml.replace("_ISSUES_", item.open_issues_count);
                    $("#list_repositorios").append(auxHtml);
                }
            }
        }
    });
}
(function($) {
    "use strict";
    $('#search_box').keyup(function(){
        var criteria = $(this).val();
        if(!criteria){
            $.get("tasks", function( data ) {
                if(data){
                    cargarTareas(data);
                }
            });
        }
        else if(criteria == "finalizado"){
            $.get("tasks/status/3", function( data ) {
                if(data){
                    cargarTareas(data);
                    
                }
            });
        }else if(criteria == "desarrollo"){
            $.get("tasks/status/1", function( data ) {
                if(data){
                    cargarTareas(data);
                }
            });
        }else{
            $.get("tasks/tasks/" + criteria, function( data ) {
                $("#list_tareas").html("");
                if(data){
                    $("#list_tareas").html("");
                    var auxHtml = $("#projects").html();
                    auxHtml = auxHtml.replace("_ID_", data.id);
                    auxHtml = auxHtml.replaceAll("_NAME_", data.name);
                    auxHtml = auxHtml.replaceAll("_DESC_", data.description);
                    auxHtml = auxHtml.replace("_STATUS_", status[data.status]);
                    auxHtml = auxHtml.replace("_COLOR_", color[data.status]);
                    auxHtml = auxHtml.replace("_WIDTH_", width[data.status]);
                    $("#list_tareas").append(auxHtml);
                }
            });
        }
    });
    initPage();
    buscarRep("day");
    function cargarTareas(data){
        $("#list_tareas").html("");
        var index = 0;
        for(index = 0; index < data.length; index++){
            var item = data[index];
            var auxHtml = $("#projects").html();
            auxHtml = auxHtml.replace("_ID_", item.id);
            auxHtml = auxHtml.replaceAll("_NAME_", item.name);
            auxHtml = auxHtml.replaceAll("_DESC_", item.description);
            auxHtml = auxHtml.replace("_STATUS_", status[item.status]);
            auxHtml = auxHtml.replace("_COLOR_", color[item.status]);
            auxHtml = auxHtml.replace("_WIDTH_", width[item.status]);
            $("#list_tareas").append(auxHtml);
        }
    }
    $("#btn_eliminar").on("click", function(e){
        e.preventDefault();
        $("#modal_tarea").modal('hide');
        $.ajax({
            url:"tasks/" + $("#id_tarea").val(),
            type:"DELETE",
            contentType:"application/json; charset=utf-8",
            dataType:"json",
            success: function() {
                initPage();
            },
        });
    });
    $("#btn_agregar").on("click", function(e){
        e.preventDefault();
        $("#estado_container").css("display", "none");
        $("#btn_eliminar").css("display", "none");
        $("#usuario_tarea").css("display", "none");
        $("#nomb_tareat").val("");
        $("#desc_tarea").val("");
        $("#id_tarea").val("");
        $("#estado_tarea").val("");
        $("#modal_tarea").modal('show');
    });
    $("#btn_guardar").on("click", function(){
        $("#modal_tarea").modal('hide');
        var name = $("#nomb_tareat").val();
        var desc = $("#desc_tarea").val();
        var idtarea = $("#id_tarea").val();
        var estado = $("#estado_tarea").val();
        if(idtarea != "_ID_TAREA_" && idtarea != ""){
            $.ajax({
                url:"tasks",
                type:"PUT",
                data: '{"id": "' + idtarea + '", "name": "' + name + ' ", "description": "' + desc + '", "status": "' + estado + '"}',
                contentType:"application/json; charset=utf-8",
                dataType:"json",
                success: function(data){
                    $("#nomb_tareat").val("");
                    $("#desc_tarea").val("");
                    $("#id_tarea").val("");
                    $("#estado_tarea").val("");
                    initPage();
                }
            })
        }else{
            $.ajax({
                url:"tasks",
                type:"POST",
                data: '{"name": "' + name + ' ", "description": "' + desc + '"}',
                contentType:"application/json; charset=utf-8",
                dataType:"json",
                success: function(data){
                    $("#nomb_tareat").val("");
                    $("#desc_tarea").val("");
                    $("#id_tarea").val("");
                    $("#estado_tarea").val("");
                    $.get("tasks", function( data ) {
                        if(data){
                            cargarTareas(data);
                        }
                    });
                }
            })
        }
    });
    $("#usuario_tarea").on("change", function(){
        var usuario = $(this).val();
        var tarea = $("#id_tarea").val();
        if(usuario == "1"){
            $.ajax({
                url:"tasks/addTask",
                type:"POST",
                data: '{"userId": "' + usuario + ' ", "tasksId": "' + tarea + '"}',
                contentType:"application/json; charset=utf-8",
                dataType:"json",
            });
        }
    });
    $("#rep_fecha").on("change", function(){
        var fecha = $(this).val();
        buscarRep(fecha);
    });
    $("#rep_fecha").on("change", function(){
        var fecha = $(this).val();
        buscarRep(fecha);
    });
    $('#search_box_rep').keyup(function(){
        var criteria = $(this).val();
        if(criteria.length > 4){
            $.ajax({
                url:"github/" + criteria,
                type:"POST",
                contentType:"application/json; charset=utf-8",
                dataType:"json",
                success: function(data){
                    if(data){
                        $("#list_repositorios").html("");
                        var index = 0;
                        for(index = 0; index < data.length; index++){
                            var item = data[index];
                            var auxHtml = $("#repositorios").html();
                            auxHtml = auxHtml.replace("_NAME_", item.name);
                            auxHtml = auxHtml.replace("_OWNER_", item.owner.login);
                            auxHtml = auxHtml.replace("_AVATAR", item.owner.avatar_url);
                            auxHtml = auxHtml.replace("_ESTRELLAS_", item.stargazers_count);
                            auxHtml = auxHtml.replace("_ISSUES_", item.open_issues_count);
                            $("#list_repositorios").append(auxHtml);
                        }
                    }
                }
            });
        }
    });
}
)(jQuery);