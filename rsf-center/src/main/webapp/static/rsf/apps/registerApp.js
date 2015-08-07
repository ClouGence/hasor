var formValidators = {
  message : 'This value is not valid',
  feedbackIcons : {
    valid : 'glyphicon glyphicon-ok',
    invalid : 'glyphicon glyphicon-remove',
    validating : 'glyphicon glyphicon-refresh'
  },
  fields : {
    appCode : {
      message : 'The username is not valid',
      validators : {
        notEmpty : {
          message : '应用唯一Code代码不能为空，这个代码十分重要！'
        },
        stringLength : {
          min : 5,
          max : 100,
          message : '请输入5～100个字符数据！'
        },
        regexp : {
          regexp : /^[a-zA-Z0-9_]+$/,
          message : '合法的输入为：“a-zA-Z0-9_”'
        }
      }
    },
    appName : {
      validators : {
        notEmpty : {
          message : '应用名不能为空，请为它起个名字吧。'
        },
        stringLength : {
          min : 3,
          max : 100,
          message : '请输入3～100个字符数据！'
        }
      }
    },
    accessKey : {
      validators : {
        stringLength : {
          min : 0,
          max : 50,
          message : '请输入0～50个字符数据！'
        }
      }
    },
    accessSecret : {
      validators : {
        stringLength : {
          min : 0,
          max : 128,
          message : '请输入0～128个字符数据！'
        }
      }
    }
  }
};
$(document).ready(
    function() {
      $('#registerForm').bootstrapValidator(formValidators).on('success.form.bv',
          function(e) {
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