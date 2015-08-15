var formValidators = {
    feedbackIcons : {
        valid : 'glyphicon glyphicon-ok',
        invalid : 'glyphicon glyphicon-remove',
        validating : 'glyphicon glyphicon-refresh'
    },
    fields : {
        bindGroup : {
	        validators : {
	            notEmpty : {
		            message : '请填写接口所属分组信息。'
	            },
	            stringLength : {
	                min : 1,
	                max : 100,
	                message : '请输入1～100个字符数据！'
	            },
	            regexp : {
	                regexp : /^[a-zA-Z0-9_]+$/,
	                message : '合法的输入为：“a-zA-Z0-9_”'
	            }
	        }
        },
        bindName : {
	        validators : {
	            notEmpty : {
		            message : '请填写接口名称信息。'
	            },
	            stringLength : {
	                min : 1,
	                max : 200,
	                message : '请输入1～200个字符数据！'
	            },
	            regexp : {
	                regexp : /^[a-zA-Z0-9_]+$/,
	                message : '合法的输入为：“a-zA-Z0-9_”'
	            }
	        }
        },
        bindType : {
	        validators : {
	            notEmpty : {
		            message : '请填写接口类型名。'
	            },
	            stringLength : {
	                min : 1,
	                max : 300,
	                message : '请输入1～300个字符数据！'
	            },
	            regexp : {
	                regexp : /^[a-zA-Z0-9_\\.]+$/,
	                message : '合法的输入为：“a-zA-Z0-9_”和“.”'
	            }
	        }
        },
        bindVersion : {
	        validators : {
	            notEmpty : {
		            message : '请填写接口版本信息。'
	            },
	            stringLength : {
	                min : 1,
	                max : 50,
	                message : '请输入1～50个字符数据！'
	            },
	            regexp : {
	                regexp : /^[a-zA-Z0-9_\\.]+$/,
	                message : '合法的输入为：“a-zA-Z0-9_”和“.”'
	            }
	        }
        },
        contactUsers : {
	        validators : {
		        stringLength : {
		            min : 0,
		            max : 200,
		            message : '最多可以输入200个字符！'
		        }
	        }
        }
    }
};
$(document).ready(function() {
	$('#registerForm').bootstrapValidator(formValidators).on('success.form.bv', function(e) {
		// Prevent form submission
		e.preventDefault();
		// Get the form instance
		var $form = $(e.target);
		// Get the BootstrapValidator instance
		var bv = $form.data('bootstrapValidator');
		// Use Ajax to submit form data
		$.post($form.attr('action'), $form.serialize(), function(result) {
			console.log(result);
		}, 'json');
	});
});