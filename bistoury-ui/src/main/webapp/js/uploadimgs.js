jQuery(function($){
    var ctx = $('.adupload');

    $('.upload-form input, .drop-upload-form input', ctx).change(function(){
        var f = this.files[0];
        if(!~f.type.indexOf('image')){
            alert('文件' + f.name + '不是图片');
            return;
        }
        if(f.size >= 512000){
            alert('您这张' + f.name + '图片大小过大，应小于500k');
            return;
        }

        var form = $(this).closest('form').hide();
        form.clone().insertBefore(form).show()
            .find('input').change(arguments.callee);

        $('<li>\
            <div class="image-item">\
                <div class="tips">\
                    <a href="javascript:" class="delete">删除</a>\
                    ' + f.name +'\
                </div>\
                <div class="result">\
                    <span class="msg"></span>\
                    <a href="javascript:" class="copy" title="点击复制图片地址">上传成功！复制图片地址</a>\
                    <span class="copied-tip">已复制</span>\
                </div>\
                <img src="about:" alt="' + f.name + '" class="image" />\
            </div>\
        </li>').appendTo($('.images-preview', ctx)).data('upload-form', form)
            .find('.image').each(function(){
                var img = this, fr = new FileReader();
                fr.onload = function(e){
                    img.src = e.target.result;
                };
                fr.readAsDataURL(f);
            });
    });
    $('.file-drop', ctx).on('dragover', function(){
        $(this).addClass('dropping');
    }).on('dragleave drop', function(){
        $(this).removeClass('dropping');
    });
    $('.images-preview', ctx).on('click', '.delete', function(){
        var i = $(this).closest('li');
        i.data('upload-form').remove();
        i.fadeOut(function(){
            $(this).remove();
        })
    });
    $('.upload', ctx).click(function(){
        $('.images-preview > li', ctx).each(function(){
            var self = $(this), form = self.data('upload-form');
            if(form == null){
                return;
            }
            self.removeData('upload-form');
            self.addClass('uploading').removeClass('error');
            $.getJSON('/ad/img/uploadImgUrl.do', function(data){
                form.attr('action', data.data);
                ajaxForm({
                    form: form,
                    cb: '/js/ajax-form-cb.html',
                    success: function(data){
                        data = data.data[0];
                        self.removeClass('uploading');
                        if(data.ret){
                            self.addClass('uploaded');
                            $('.copy', self)
                                .attr('data-clipboard-text', 'http://img1.qunarzz.com' + data.img)
                                .each(function(){
                                    new ZeroClipboard(this).on('complete', copied);
                                });
                        }else{
                            self.addClass('error').data('upload-form', form)
                                .find('.result .msg').html('上传失败！请重新点击上传图片！')

                        }
                    }
                });
            });
        });
    });

    var callbackN = +new Date();
    function ajaxForm(opts){
        var f = $(opts.form),
            n = 'ajax-form-cb' + callbackN++,
            frame = $('<iframe style="display: none" name="' + n + '"></iframe>').appendTo('body');
        window[n] = function(query){
            var data = {};
            frame.remove();
            delete window[n];
            $(query.slice(1).split('&')).each(function(){
                var offset = this.indexOf('=');
                data[this.slice(0, offset)] = decodeURIComponent(this.slice(offset + 1));
            });
            opts.success(JSON.parse(data.r));
        };
        var action = f.data('orig-action') || f.attr('action');
        if(f.data('orig-action') == null){
            f.data('orig-action', action);
        }
        f.attr({
            target: n,
            action: action + (~action.indexOf('?') ? '&' : '?') + $.param({
                cb: location.origin + opts.cb + '?cb=' + n
            })
        });
        f[0].submit();
    }

    function copied(){
        $('.copied', ctx).removeClass('copied');
        $(this).closest('li').addClass('copied');
    }
});

