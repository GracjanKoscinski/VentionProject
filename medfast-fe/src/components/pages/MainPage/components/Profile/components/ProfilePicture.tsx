import React, { useEffect, useState, ChangeEvent } from 'react';
import { GetProfilePicture } from '@/api/userProfile/GetProfilePicture';
import { useUser } from '@/utils/UserContext';
import { Input, Label, RoundImageBackground, ServerResponsePopUp } from '@/components/common';
import NoProfile from '@/components/Icons/resources/no_profile.png';
import { UploadProfilePicture } from '@/api/userProfile/UploadProfilePicture';
import { FileInput, PointerLabel, Wrapper } from './styles';
import { DeleteProfilePicture } from '@/api/userProfile/DeleteProfilePicture';

const ImageDisplay = () => {
  const [imageUrl, setImageUrl] = useState<string | null>(null);
  const [file, setFile] = useState<File | null>(null);
  const [serverResponse, setServerResponse] = useState<
    'hasBeenUpdated' | 'somethingWrong' | 'wrongFileExtension' | null
  >(null);

  const userAuth = useUser();
  const [editPhoto, setEditPhoto] = useState<boolean>(false);

  const fetchImage = async () => {
    try {
      const blob = await GetProfilePicture(userAuth.userData?.accessToken || '');
      const imageUrl = URL.createObjectURL(blob);
      setImageUrl(imageUrl);
    } catch (err: any) {
      if (err.message !== '404') {
        setServerResponse('somethingWrong');
      }
      setImageUrl(null);
    }
  };

  useEffect(() => {
    fetchImage();
    return () => {
      if (imageUrl) {
        URL.revokeObjectURL(imageUrl);
      }
    };
  }, []);

  const isValidFileExtension = (fileName: string) => {
    const validExtensions = ['jpg', 'jpeg', 'png', 'gif'];
    const fileExtension = fileName.split('.').pop()?.toLowerCase();
    return fileExtension && validExtensions.includes(fileExtension);
  };

  const handleFileChange = async (event: ChangeEvent<HTMLInputElement>) => {
    const selectedFile = event.target.files && event.target.files[0];
    if (selectedFile) {
      if (!isValidFileExtension(selectedFile.name)) {
        setServerResponse('wrongFileExtension');
        return;
      }

      setFile(selectedFile);
      try {
        await handleUpload(selectedFile);
      } catch (err: any) {
        setServerResponse('somethingWrong');
      }
    }
  };

  const handleUpload = async (selectedFile: File) => {
    const formData = new FormData();
    formData.append('photo', selectedFile);
    try {
      const response = await UploadProfilePicture(
        userAuth.userData?.accessToken || '',
        selectedFile,
      );
      setServerResponse('hasBeenUpdated');
      setEditPhoto(false);
      await fetchImage();
    } catch (err: any) {
      setServerResponse('somethingWrong');
    }
  };

  const handleDelete = async () => {
    try {
      const response = await DeleteProfilePicture(userAuth.userData?.accessToken || '');
      setServerResponse('hasBeenUpdated');
      setImageUrl(null);
      setEditPhoto(false);
    } catch (err: any) {
      setServerResponse('somethingWrong');
    }
  };

  return (
    <div>
      <ServerResponsePopUp
        serverResponse={serverResponse}
        onClick={() => setServerResponse(null)}
      />

      <RoundImageBackground
        $backgroundImage={imageUrl ? imageUrl : NoProfile}
        $backgroundColor="white"
        $borderColor="white"
        style={{ width: '200px', height: '200px', borderRadius: '50%' }}
      />

      {!editPhoto && (
        <PointerLabel>
          <Label
            label="Edit photo"
            fontWeight={500}
            fontSize="s"
            lineHeight="22px"
            color="purple"
            margin="0"
            onClick={() => setEditPhoto(true)}
          />
        </PointerLabel>
      )}

      {editPhoto && (
        <div>
          <PointerLabel>
            <FileInput htmlFor="fileInput">Choose from the gallery</FileInput>
          </PointerLabel>
          <input
            type="file"
            id="fileInput"
            name="profilePicture"
            onChange={handleFileChange}
            style={{ display: 'none' }}
          />
          {imageUrl && (
            <PointerLabel>
              <Label
                label="Delete Picture"
                fontWeight={500}
                fontSize="s"
                lineHeight="22px"
                color="purple"
                margin="0"
                onClick={() => handleDelete()}
              />
            </PointerLabel>
          )}
          <PointerLabel>
            <Label
              label="Cancel"
              fontWeight={500}
              fontSize="s"
              lineHeight="22px"
              color="purple"
              margin="0"
              onClick={() => setEditPhoto(false)}
            />
          </PointerLabel>
        </div>
      )}
    </div>
  );
};

export default ImageDisplay;

