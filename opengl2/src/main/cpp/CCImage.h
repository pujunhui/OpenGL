#ifndef CCIMAGE_H
#define CCIMAGE_H

#include "CCNDKLogDef.h"

#ifndef U8_t
#define U8_t unsigned char
#endif

class CCImage {

public:
    CCImage();
    ~CCImage();

    int         GetWidth() const;
    int         GetHeight() const;
    int         GetType() const ;
    U8_t*       GetData() const;

    void        ReadFromFile(U8_t* fileName);
    void        ReadFromBuffer(U8_t* dataBuff,int length);

private:
    int         m_width;
    int         m_height;
    int         m_type;
    U8_t*       m_pImgData;
};

#endif // CCIMAGE_H
