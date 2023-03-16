package cn.bywin.business.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class Code {
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    public BufferedImage getCode(){
        int width = 200;
        int height= 60;
        //生成对应宽高的初始图片
        BufferedImage verifyImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        //调用方法把画板给他，生成验证码字符串，加上随机线和噪点，返回值为验证码字符串
        String ranCode = new Code().drawRandomText(width,height,verifyImg);
        //将验证码缓存到redis
        redisTemplate.opsForValue().set(ranCode.toLowerCase(),ranCode.toLowerCase(), Duration.ofMinutes(5));

        return verifyImg;
    }

    //图片处理
    public String drawRandomText(int width,int height,BufferedImage verifyImg){
        //在传过来的画板上开一个2d画笔
        Graphics2D graphics =(Graphics2D)verifyImg.getGraphics();
        //设置画笔颜色
        graphics.setColor(Color.white);//这个给验证码背景好了
        //用笔填充背景操作指定范围
        graphics.fillRect(0,0,width,height);
        //设置字体
        graphics.setFont(new Font("黑体",Font.BOLD,40));
        //设置一个字符池
        String baseNumLetter="1234567890";//123456789QWERTYUPASDFGHKLXCVBNM

        StringBuffer stringBuffer = new StringBuffer();
        int x= 10;//原点的坐标
        //预设一个空字符
        String ch = "";
        //来一个循环一次循环生成一个随机code字符
        Random ran=new Random();
        for (int i = 0; i < 4; i++) {
            //先获取一个随机颜色,设置在画笔对象上
            graphics.setColor(getRandomColor());
            int degree = ran.nextInt()%30;//角度小于30度，生成随机+-30范围百，这里用来表示角度
            //基于字符池长度生成随机索引，一会取字符用
            int dot = ran.nextInt(baseNumLetter.length());
            //根据随机取出的下标索引随机区一个字符
            ch =baseNumLetter.charAt(dot)+"";
            //将取出的字符追加上去。这里只是收集验证码最终结果而已，方便一会返回，下面写入的时候是按照临时的ch写入
            stringBuffer.append(ch);
            //操纵画笔正向旋转degree度。上面定义好的一个数字X为X轴坐标
            graphics.rotate(degree*Math.PI/180,x,45);
            //操纵画笔写下字符（内容，X轴，Y轴）
            graphics.drawString(ch,x,45);
            //将画笔旋转回来
            graphics.rotate(-degree*Math.PI/180,x,45);
            //推进画笔的x轴值。
            x+=48;
        }
        //循环结束5个字符写上去了
        //画干扰线画几条随便
        for (int i = 0; i < 8; i++) {
            //给画笔涂上随机颜色
            graphics.setColor(getRandomColor());
            //操纵画笔随机画线,传参设置线条的长度宽度距离边缘的长度宽度
            graphics.drawLine(ran.nextInt(width),ran.nextInt(height),ran.nextInt(width),ran.nextInt(height));
        }
        //添加噪点,这个多整点
        for (int i = 0; i < 40; i++) {
            int xx = ran.nextInt(width);
            int yy = ran.nextInt(height);
            //给画笔涂上随机颜色
            graphics.setColor(getRandomColor());
            //下手画点，给出上面定义好的位置坐标,以及像素宽度长度
            graphics.fillRect(xx,yy,2,2);
        }
        //画板已经画完了，顺便将收集好的验证码return出去；
        return stringBuffer.toString();
    }


    //获取随机颜色的方法
    private Color getRandomColor() {
        Random ran=new Random();
        Color color=new Color(ran.nextInt(256),ran.nextInt(256),ran.nextInt(256));
        return color;
    }

    public String check(String id) {
       return redisTemplate.opsForValue().get(id);
    }
}
